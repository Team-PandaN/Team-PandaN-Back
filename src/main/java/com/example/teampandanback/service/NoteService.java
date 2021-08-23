package com.example.teampandanback.service;

import com.example.teampandanback.domain.Comment.CommentRepository;
import com.example.teampandanback.domain.bookmark.Bookmark;
import com.example.teampandanback.domain.bookmark.BookmarkRepository;
import com.example.teampandanback.domain.file.File;
import com.example.teampandanback.domain.file.FileRepository;
import com.example.teampandanback.domain.note.MoveStatus;
import com.example.teampandanback.domain.note.Note;
import com.example.teampandanback.domain.note.NoteRepository;
import com.example.teampandanback.domain.note.Step;
import com.example.teampandanback.domain.project.Project;
import com.example.teampandanback.domain.project.ProjectRepository;
import com.example.teampandanback.domain.user.User;
import com.example.teampandanback.domain.user_project_mapping.UserProjectMapping;
import com.example.teampandanback.domain.user_project_mapping.UserProjectMappingRepository;
import com.example.teampandanback.dto.file.request.FileDetailRequestDto;
import com.example.teampandanback.dto.file.response.FileDetailResponseDto;
import com.example.teampandanback.dto.note.request.NoteCreateRequestDto;
import com.example.teampandanback.dto.note.request.NoteMoveRequestDto;
import com.example.teampandanback.dto.note.request.NoteUpdateRequestDto;
import com.example.teampandanback.dto.note.response.*;
import com.example.teampandanback.exception.ApiRequestException;
import com.example.teampandanback.utils.CustomPageImpl;
import com.example.teampandanback.utils.PandanUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class NoteService {
    private final NoteRepository noteRepository;
    private final UserProjectMappingRepository userProjectMappingRepository;
    private final ProjectRepository projectRepository;
    private final BookmarkRepository bookmarkRepository;
    private final CommentRepository commentRepository;
    private final FileRepository fileRepository;
    private final PandanUtils pandanUtils;
    private final LockManagerService lockManagerService;

    // Note 상세 조회
    @Transactional
    public NoteDetailResponseDto readNoteDetail(Long noteId, User currentUser) {
        // Note 조회
        NoteResponseDto noteResponseDto = noteRepository.findByNoteId(noteId)
                .orElseThrow(() -> new ApiRequestException("작성된 노트가 없습니다."));

        // 노트가 유저가 참여하고 있는 Project 에 있는지 확인
        checkUserProject(currentUser.getUserId(), noteResponseDto.getProjectId());

        // 유저가 노트를 북마크 했는지 여부
        Optional<Bookmark> bookmark = bookmarkRepository.findByUserIdAndNoteId(currentUser.getUserId(), noteId);
        noteResponseDto.setBookmark(bookmark.isPresent());

        // 노트에 있는 파일 조회
        List<File> fileList = fileRepository.findFilesByNoteId(noteId);
        List<FileDetailResponseDto> fileDetailResponseDtoList = new ArrayList<>();
        for (File fileUnit : fileList) {
            fileDetailResponseDtoList.add(FileDetailResponseDto.fromEntity(fileUnit));
        }

        return NoteDetailResponseDto.fromEntity(noteResponseDto, fileDetailResponseDtoList);
    }

    // Note 상세 조회에서 내용 업데이트
    @Transactional
    public NoteUpdateResponseDto updateNoteDetail(Long noteId, User currentUser, NoteUpdateRequestDto noteUpdateRequestDto) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ApiRequestException("수정 할 노트가 없습니다."));

        // 수정하려는 노트가 유저가 참여하고 있는 Project 에 있는지 확인
        userProjectMappingRepository
                .findByUserIdAndProjectId(currentUser.getUserId(), note.getProject().getProjectId())
                .orElseThrow(() -> new ApiRequestException("노트가 있는 프로젝트에 소속된 유저가 아니여서 노트를 수정하실 수 없습니다."));

        List<FileDetailRequestDto> files = new ArrayList<>(noteUpdateRequestDto.getFiles());
        files.stream()
                .map(file -> new File(file.getFileName(), file.getFileUrl(), currentUser, note))
                .forEach(fileRepository::save);

        List<File> fileList = fileRepository.findFilesByNoteId(noteId);
        if (fileList.size() > pandanUtils.limitOfFile()) {
            throw new ApiRequestException(pandanUtils.messageForLimitOfFile());
        }

        List<FileDetailResponseDto> fileDetailResponseDtoList = new ArrayList<>();
        for (File file : fileList) {
            fileDetailResponseDtoList.add(FileDetailResponseDto.fromEntity(file));
        }

        note.update(noteUpdateRequestDto, pandanUtils.changeType(noteUpdateRequestDto.getDeadline()));

        NoteUpdateResponseDto noteUpdateResponseDto = NoteUpdateResponseDto.of(note);
        noteUpdateResponseDto.uploadFile(fileDetailResponseDtoList);

        return noteUpdateResponseDto;
    }

    // Note 칸반 이동 시 순서 업데이트
    @Transactional
    public NoteUpdateResponseDto moveNote(Long noteId, NoteMoveRequestDto noteMoveRequestDto, User currentUser) {

        // 수정하려는 노트가 존재하지 않으면 Exception 반환.
        Note currentNote = noteRepository.findById(noteId).orElseThrow(() -> new ApiRequestException("수정하려는 노트가 존재하지 않음"));

        // 수정하려는 노트가 유저가 참여하고 있는 Project 에 있는지 확인
        userProjectMappingRepository
                .findByUserIdAndProjectId(currentUser.getUserId(), currentNote.getProject().getProjectId())
                .orElseThrow(() -> new ApiRequestException("노트가 있는 프로젝트에 소속된 유저가 아니여서 노트를 이동하실 수 없습니다."));

        // Project로 전체 노트 리스트 가져오기
        List<Note> rawNoteList = noteRepository.findByProject(currentNote.getProject());

        // DB와 프론트에서 보낸 싱크가 맞는지 검증하기 위해 rawMap 만들기
        Map<Long, Note> rawMap = pandanUtils.getRawMap(rawNoteList);

        // from과 To 그리고 currentNote의 연결 관계 검증
        if (!pandanUtils.checkSync(noteId, noteMoveRequestDto, rawNoteList, rawMap)) {
            throw new ApiRequestException("새로 고침이 필요합니다.");
        }

        Long fromPreNoteId = noteMoveRequestDto.getFromPreNoteId();
        Long fromNextNoteId = noteMoveRequestDto.getFromNextNoteId();
        Long toPreNoteId = noteMoveRequestDto.getToPreNoteId();
        Long toNextNoteId = noteMoveRequestDto.getToNextNodeId();

        // 노트가 이동하는 16가지 상황 중 어떤 상황인지를 파악한다.
        MoveStatus[] moveStatuses = pandanUtils.getMoveStatus(noteMoveRequestDto);

        // From 에서 fromPre와 fromNext 연결관계 정리한다.
        switch (moveStatuses[0]) {
            case UNIQUE:
                break;
            case CURRENTTOP:
                rawMap.get(fromPreNoteId).updateNextId(0L);
                break;
            case CURRENTBOTTOM:
                rawMap.get(fromNextNoteId).updatePreviousId(0L);
                break;
            case CURRENTBETWEEN:
                rawMap.get(fromPreNoteId).updateNextId(fromNextNoteId);
                rawMap.get(fromNextNoteId).updatePreviousId(fromPreNoteId);
                break;
        }

        // To 에서 toPre와 toNext 연결관계 정리한다.
        switch (moveStatuses[1]) {
            case UNIQUE:
                currentNote.updatePreviousIdAndNextId(0L, 0L);
                break;
            case CURRENTTOP:
                rawMap.get(toPreNoteId).updateNextId(currentNote.getNoteId());
                currentNote.updatePreviousIdAndNextId(toPreNoteId, 0L);
                break;
            case CURRENTBOTTOM:
                currentNote.updatePreviousIdAndNextId(0L, toNextNoteId);
                rawMap.get(toNextNoteId).updatePreviousId(currentNote.getNoteId());
                break;
            case CURRENTBETWEEN:
                rawMap.get(toPreNoteId).updateNextId(currentNote.getNoteId());
                currentNote.updatePreviousIdAndNextId(toPreNoteId, toNextNoteId);
                rawMap.get(toNextNoteId).updatePreviousId(currentNote.getNoteId());
                break;
        }

        // Step 이동이든 아니든 DynamicUpdate에 의해서 이동한 경우만 반영하게 된다.
        currentNote.updateStepWhileMoveNote(Step.valueOf(noteMoveRequestDto.getStep()));
        return NoteUpdateResponseDto.of(currentNote);
    }

    // Note 작성
    @Transactional
    public NoteCreateResponseDto createNote(Long projectId, NoteCreateRequestDto noteCreateRequestDto, User currentUser) {
        //프로젝트에 쓰여진 노트 총 갯수
        Long theNumberOfNoteWroteToProject = noteRepository.countByProjectId(projectId);

        Long theNumberOfNoteWroteToProjectUpperBound = 100L;
        if(theNumberOfNoteWroteToProject >= theNumberOfNoteWroteToProjectUpperBound){
            throw new ApiRequestException("프로젝트에 이미 "+theNumberOfNoteWroteToProjectUpperBound+"개의 노트가 작성되어있습니다.");
        }
        // 유저가 해당 프로젝트에 참여하고 있는지 확인
        UserProjectMapping userProjectMapping = checkUserProject(currentUser.getUserId(), projectId);

        LocalDate deadline = pandanUtils.changeType(noteCreateRequestDto.getDeadline());
        Step step = Step.valueOf(noteCreateRequestDto.getStep());
        User user = userProjectMapping.getUser();
        Project project = userProjectMapping.getProject();

        // Project로 전체 노트 리스트 가져오기
        List<Note> rawNoteList = noteRepository.findByProject(project);

        // topNoteList로부터 topNote 찾기, 없다면 null 넣는다.
        Note topNote = pandanUtils.getTopNoteList(rawNoteList)
                .stream()
                .filter(note -> note.getStep().equals(step))
                .findFirst().orElse(null);

        List<FileDetailRequestDto> files = new ArrayList<>(noteCreateRequestDto.getFiles());

        // topNote가 있다면, topNote의 pre와 next 바꿔주고 dtoNote 저장한다.
        if (topNote != null) {
            Note savedNote = noteRepository.save(Note
                    .of(noteCreateRequestDto, deadline, step, user, project, topNote.getNoteId(), 0L));
            topNote.updatePreviousIdAndNextId(topNote.getPreviousId(), savedNote.getNoteId());
            NoteCreateResponseDto noteCreateResponseDto = NoteCreateResponseDto.of(savedNote);
            files.stream()
                    .map(file -> new File(file.getFileName(), file.getFileUrl(), user, savedNote))
                    .forEach(fileRepository::save);
            if (files.size() > pandanUtils.limitOfFile()) {
                throw new ApiRequestException(pandanUtils.messageForLimitOfFile());
            }
            noteCreateResponseDto.uploadFile(files);
            return noteCreateResponseDto;
        }
        // topNote 없다면, 그냥 저장한다
        else {
            Note savedNote = noteRepository.save(Note
                    .of(noteCreateRequestDto, deadline, step, user, project, 0L, 0L));
            NoteCreateResponseDto noteCreateResponseDto = NoteCreateResponseDto
                    .of(savedNote);
            files.stream()
                    .map(file -> new File(file.getFileName(), file.getFileUrl(), user, savedNote))
                    .forEach(fileRepository::save);
            if (files.size() > pandanUtils.limitOfFile()) {
                throw new ApiRequestException(pandanUtils.messageForLimitOfFile());
            }
            noteCreateResponseDto.uploadFile(files);
            return noteCreateResponseDto;
        }
    }

    // 해당 Project 에서 내가 작성한 Note 조회
    public NoteMineInProjectResponseDto readNotesMineOnly(Long projectId, User currentUser, int page, int size) {

        // 유저가 참여하고 있는 Project 인지 확인
        checkUserProject(currentUser.getUserId(), projectId);

        // 해당 Project 에서 내가 작성한 Note 조회
        CustomPageImpl<Note> noteCustomPage = noteRepository.findAllNoteByProjectAndUserOrderByCreatedAtDesc(
                projectId, currentUser.getUserId(), pandanUtils.dealWithPageRequestParam(page, size));

        List<NoteReadMineEachResponseDto> myNoteList = noteCustomPage.toList()
                .stream()
                .map(NoteReadMineEachResponseDto::fromEntity)
                .collect(Collectors.toList());

        return NoteMineInProjectResponseDto.fromEntity(myNoteList, noteCustomPage);
    }

    // 전체 Project 에서 내가 북마크한 Note 조회
    public NoteBookmarkedResponseDto readBookmarkedMine(User currentUser, int page, int size) {

        // 해당 북마크한 Note 조회
        CustomPageImpl<NoteEachBookmarkedResponseDto> noteEachBookmarkCustomPage =
                bookmarkRepository.findNoteByUserIdInBookmark(
                        currentUser.getUserId(), pandanUtils.dealWithPageRequestParam(page, size));

        return NoteBookmarkedResponseDto.fromEntity(noteEachBookmarkCustomPage.toList(), noteEachBookmarkCustomPage);
    }

    // Note 삭제
    @Transactional
    public NoteDeleteResponseDto deleteNote(Long noteId, User currentUser) {

        // 삭제할 Note 조회
        Note note = noteRepository.findById(noteId).orElseThrow(
                () -> new ApiRequestException("이미 삭제된 노트입니다.")
        );

        // 삭제할 Note 가 유저가 참여하고 있는 Project 에 있는지 확인
        userProjectMappingRepository
                .findByUserIdAndProjectId(currentUser.getUserId(), note.getProject().getProjectId())
                .orElseThrow(() -> new ApiRequestException("노트가 있는 프로젝트에 소속된 유저가 아니여서 노트를 삭제하실 수 없습니다."));

        //Note에 연관된 파일 삭제
        fileRepository.deleteFileByNoteId(noteId);

        // Note에 연관된  코멘트 삭제
        commentRepository.deleteCommentByNoteId(noteId);

        // Note 에 연관된 북마크 삭제
        bookmarkRepository.deleteByNote(noteId);

        Note previousNote = noteRepository.findById(note.getPreviousId()).orElse(null);
        Note nextNote = noteRepository.findById(note.getNextId()).orElse(null);

        MoveStatus deleteStatus = pandanUtils.getDeleteStatus(note.getPreviousId(), note.getNextId());

        switch (deleteStatus) {
            case UNIQUE:
                break;
            case CURRENTTOP:
                previousNote.updateNextId(0L);
                break;
            case CURRENTBOTTOM:
                nextNote.updatePreviousId(0L);
                break;
            case CURRENTBETWEEN:
                previousNote.updateNextId(nextNote.getNoteId());
                nextNote.updatePreviousId(previousNote.getNoteId());
                break;
        }

        // Note 삭제
        noteRepository.delete(note);

        return NoteDeleteResponseDto.builder()
                .noteId(noteId)
                .build();
    }

    // Note 칸반형 조회 (칸반 페이지)
    @Transactional
    public KanbanNoteSearchResponseDto readKanbanNote(Long projectId, User currentUser) {

        // 유저가 참여하고 있는 Project 인지 확인
        UserProjectMapping userProjectMapping = checkUserProject(currentUser.getUserId(), projectId);

        // Project로 전체 노트 리스트 가져오기
        List<Note> rawNoteList = noteRepository.findByProject(userProjectMapping.getProject());

        // topNoteList 만들기, <PK,Note> 해쉬맵 만들기 (실은 순회 한 번에 할 수 있음, 지금은 2번) -> 수정 시 재사용 가능
        List<Note> topNoteList = pandanUtils.getTopNoteList(rawNoteList);
        Map<Long, Note> rawMap = pandanUtils.getRawMap(rawNoteList);

        //각 스텝 별 노트리스트를 담은 통합 리스트 연결리스트 순서에 맞게 재구성하여 가져온다
        List<List<KanbanNoteEachResponseDto>> resultList = pandanUtils.getResultList(topNoteList, rawMap);

        // Note 를 각 상태별로 List 로 묶어서 응답 보내기
        List<NoteOfProjectResponseDto> noteOfProjectResponseDtoList = new ArrayList<>();

        // Step 별로 순회돌기 위해서 리스트 만들기
        List<Step> stepList = new ArrayList<>(Arrays.asList(Step.STORAGE, Step.TODO, Step.PROCESSING, Step.DONE));

        // Step 별로 순회돌며 스텝 별 리스트에 resultList에 스텝 순서에 맞춰 들어간 정보를 가져온다
        for (Step step : stepList) {
            noteOfProjectResponseDtoList.add(NoteOfProjectResponseDto.of(step, resultList.get(step.getId())));
        }

        return KanbanNoteSearchResponseDto.builder()
                .noteOfProjectResponseDtoList(noteOfProjectResponseDtoList)
                .build();
    }

    // Note 일반형 조회 (파일 페이지)
    @Transactional
    public NoteSearchResponseDto readOrdinaryNote(Long projectId, int page, int size, User currentUser) {
        List<OrdinaryNoteEachResponseDto> ordinaryNoteEachResponseDtoList = new ArrayList<>();

        // 유저가 참여하고 있는 Project 인지 확인
        UserProjectMapping userProjectMapping = checkUserProject(currentUser.getUserId(), projectId);

        CustomPageImpl<Note> ordinaryNoteCustomPage = noteRepository.findAllByProjectOrderByCreatedAtDesc(
                userProjectMapping.getProject(), pandanUtils.dealWithPageRequestParam(page, size));

        for (Note note : ordinaryNoteCustomPage.toList()) {
            ordinaryNoteEachResponseDtoList.add((OrdinaryNoteEachResponseDto.fromEntity(note)));
        }

        return NoteSearchResponseDto.fromEntity(ordinaryNoteEachResponseDtoList, ordinaryNoteCustomPage);
    }

    // 전체 프로젝트에서 내가 작성한 노트 조회
    public NoteMineInTotalResponseDto readMyNoteInTotalProject(User currentUser, int page, int size) {

        // 현재 내가 참여하고있는 프로젝트 id 목록 조회
        List<Long> projectIdList = userProjectMappingRepository.findProjectIdListByUserId(currentUser.getUserId());

        // 내가 작성한 노트 조회 with 페이징
        CustomPageImpl<NoteEachMineInTotalResponseDto> totalNoteCustomPage =
                noteRepository.findUserNoteInTotalProject(
                        currentUser.getUserId(), pandanUtils.dealWithPageRequestParam(page, size), projectIdList);

        return NoteMineInTotalResponseDto.fromEntity(totalNoteCustomPage.toList(), totalNoteCustomPage);
    }

    // 내가 소속된 프로젝트에서 제목으로 노트 검색
    public NoteSearchInTotalResponseDto searchNoteInMyProjects(User currentUser, String rawKeyword) {
        List<String> keywordList = pandanUtils.parseKeywordToList(rawKeyword);
        List<noteEachSearchInTotalResponseDto> resultList = noteRepository.findNotesByUserIdAndKeywordInTotal(currentUser.getUserId(), keywordList);
        return NoteSearchInTotalResponseDto.builder().noteList(resultList).build();
    }

    // 내가 작성한 문서들 중에서 제목으로 노트 검색
    public NoteSearchInMineResponseDto searchNoteInMyNotes(User currentUser, String rawKeyword) {
        // 현재 내가 참여하고있는 프로젝트 id 목록 조회
        List<Long> projectIdList = userProjectMappingRepository.findProjectIdListByUserId(currentUser.getUserId());

        // 검색을 위해 받은 keyword 를 적절한 조건으로 parsing
        List<String> keywordList = pandanUtils.parseKeywordToList(rawKeyword);

        // 내가 작성한 노트 내에서 parsing 한 keyword 를 이용해서 검색
        List<NoteEachSearchInMineResponseDto> resultList =
                noteRepository.findNotesByUserIdAndKeywordInMine(currentUser.getUserId(), keywordList, projectIdList);

        return NoteSearchInMineResponseDto.builder().noteList(resultList).build();
    }

    // 해당 유저가 참여하고 있는 Project 인지 확인
    private UserProjectMapping checkUserProject(Long userId, Long projectId){

        // 유저가 참여하고 있는 Project 인지 확인, 해당 Project 가 실제 존재하는지도 함께 확인 가능
        return userProjectMappingRepository
                .findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new ApiRequestException("해당 프로젝트에 소속된 유저가 아닙니다."));
    }

    public Boolean isLock(Long noteId) {
        Note note = noteRepository.findById(noteId).orElseThrow(
                ()-> new ApiRequestException("잠금 여부를 알아볼 노트가 없습니다.")
        );

        if(note.getLocked()){
            return true;
        }else{
            return false;
        }
    }

    @Transactional
    public void writing(Long noteId) {
        Note note = noteRepository.findById(noteId).orElseThrow(
                ()->new ApiRequestException("해당 게시글이 없습니다.")
        );
        note.setWriting(true);
    }

    public void initLockManager(Long noteId) throws InterruptedException {
        Note note = noteRepository.findById(noteId).orElseThrow(
                ()-> new ApiRequestException("해당 노트가 없습니다.")
        );

        if(note.getLocked() == Boolean.TRUE){
            throw new ApiRequestException("잠겨있는 노트입니다.");
        }

        System.out.println("락매니저 시작");
        lockManagerService.preProcess(noteId);
        while(true){
            System.out.println("자러감");
            Thread.sleep(7000);
            System.out.println("일어남");
            if(lockManagerService.isAnyoneWriting(noteId)){
                System.out.println("자고일어나서도?");
                lockManagerService.assumeThatNobodyIsWriting(noteId);
            }else{
                System.out.println("delock");
                lockManagerService.deLock(noteId);
                break;
            }
        }
    }

}
