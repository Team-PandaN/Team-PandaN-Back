package com.example.teampandanback.service;

import com.example.teampandanback.domain.Comment.CommentRepository;
import com.example.teampandanback.domain.bookmark.Bookmark;
import com.example.teampandanback.domain.bookmark.BookmarkRepository;
import com.example.teampandanback.domain.note.Note;
import com.example.teampandanback.domain.note.NoteRepository;
import com.example.teampandanback.domain.note.Step;
import com.example.teampandanback.domain.project.Project;
import com.example.teampandanback.domain.project.ProjectRepository;
import com.example.teampandanback.domain.user.User;
import com.example.teampandanback.domain.user_project_mapping.UserProjectMapping;
import com.example.teampandanback.domain.user_project_mapping.UserProjectMappingRepository;
import com.example.teampandanback.dto.note.request.NoteCreateRequestDto;
import com.example.teampandanback.dto.note.request.NoteMoveRequestDto;
import com.example.teampandanback.dto.note.request.NoteUpdateRequestDto;
import com.example.teampandanback.dto.note.response.*;
import com.example.teampandanback.dto.note.response.noteEachSearchInTotalResponseDto;
import com.example.teampandanback.dto.note.response.NoteSearchInTotalResponseDto;
import com.example.teampandanback.exception.ApiRequestException;
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
    private final PandanUtils pandanUtils;

    // Note 상세 조회
    @Transactional
    public NoteResponseDto readNoteDetail(Long noteId, User currentUser) {
        NoteResponseDto noteResponseDto = noteRepository.findByNoteId(noteId)
                .orElseThrow(() -> new ApiRequestException("작성된 노트가 없습니다."));

        Optional<Bookmark> bookmark = bookmarkRepository.findByUserIdAndNoteId(currentUser.getUserId(), noteId);
        noteResponseDto.setBookmark(bookmark.isPresent());
        return noteResponseDto;
    }

    // Note 상세 조회에서 내용 업데이트
    @Transactional
    public NoteUpdateResponseDto updateNoteDetail(Long noteId, NoteUpdateRequestDto noteUpdateRequestDto) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ApiRequestException("수정 할 노트가 없습니다."));

        note.update(noteUpdateRequestDto, pandanUtils.changeType(noteUpdateRequestDto.getDeadline()),
                Step.valueOf(noteUpdateRequestDto.getStep()));

        return NoteUpdateResponseDto.of(note);
    }

    // Note 칸반 이동 시 순서 업데이트
    @Transactional
    public NoteUpdateResponseDto updateNoteMove(Long noteId, NoteMoveRequestDto noteMoveRequestDto) {
        // 1. Step이 안 바뀐다면~
        // 2. Step이 바뀐다면~

        // 수정하려는 노트가 존재하지 않으면 멈춘다.
        Note currentNote = noteRepository.findById(noteId).orElseThrow(() -> new ApiRequestException("수정하려는 노트가 존재하지 않음"));

        // 옮기기 전 step에서의 노트 간 연결이 db와 같은지 확인
        if (!currentNote.getPreviousId().equals(noteMoveRequestDto.getOriginPreNoteId()) || !currentNote.getNextId().equals(noteMoveRequestDto.getOriginNextNoteId())) {
            throw new ApiRequestException("새로고침 후 시도해주세요");
        }

        // Step이 바뀐다면
        if (!currentNote.getStep().toString().equals(noteMoveRequestDto.getStep())) {
            // 옮기려고 하는 step에서의 노트 간 연결이 db와 같은지 확인
            if (noteMoveRequestDto.getGoalPreNoteId() != 0L && noteMoveRequestDto.getGoalNextNoteId() != 0L) {
                Note goalPreNote = noteRepository.findById(noteMoveRequestDto.getGoalPreNoteId()).orElseThrow(() -> new ApiRequestException("수정하려는 노트가 존재하지 않음"));
                Note goalNextNote = noteRepository.findById(noteMoveRequestDto.getGoalNextNoteId()).orElseThrow(() -> new ApiRequestException("수정하려는 노트가 존재하지 않음"));
                if (goalPreNote.getNextId().equals(noteMoveRequestDto.getGoalNextNoteId())) {
                    goalPreNote.updatePreviousIdAndNextId(goalPreNote.getPreviousId(), currentNote.getNoteId());
                    currentNote.updatePreviousIdAndNextId(goalPreNote.getNoteId(), goalNextNote.getNoteId());
                    currentNote.updateStepWhileMoveNote(goalPreNote.getStep());
                    goalNextNote.updatePreviousIdAndNextId(currentNote.getNoteId(), goalNextNote.getNextId());
                } else {
                    throw new ApiRequestException("새로고침 후 시도해주세요.");
                }
            } else if (noteMoveRequestDto.getGoalPreNoteId() == 0L && noteMoveRequestDto.getGoalNextNoteId() != 0L) {
                Note goalNextNote = noteRepository.findById(noteMoveRequestDto.getGoalNextNoteId()).orElseThrow(() -> new ApiRequestException("수정하려는 노트가 존재하지 않음"));
                if (goalNextNote.getPreviousId().equals(0L)) {
                    currentNote.updatePreviousIdAndNextId(0L, goalNextNote.getNoteId());
                    currentNote.updateStepWhileMoveNote(goalNextNote.getStep());
                    goalNextNote.updatePreviousIdAndNextId(currentNote.getNoteId(), goalNextNote.getNextId());
                } else {
                    throw new ApiRequestException("새로고침 후 시도해주세요.");
                }
            } else if (noteMoveRequestDto.getGoalPreNoteId() != 0L && noteMoveRequestDto.getGoalNextNoteId() == 0L) {
                Note goalPreNote = noteRepository.findById(noteMoveRequestDto.getGoalPreNoteId()).orElseThrow(() -> new ApiRequestException("수정하려는 노트가 존재하지 않음"));
                if (goalPreNote.getNextId().equals(0L)) {
                    goalPreNote.updatePreviousIdAndNextId(goalPreNote.getPreviousId(), currentNote.getNextId());
                    currentNote.updatePreviousIdAndNextId(goalPreNote.getNoteId(), 0L);
                    currentNote.updateStepWhileMoveNote(goalPreNote.getStep());
                } else {
                    throw new ApiRequestException("새로고침 후 시도해주세요.");
                }
            }
            // TODO 목표 스텝에 아무것도 없다면 아무것도 없는지 확인
            else {
                List<Note> resultList = noteRepository.findAllByProjectAndStep(currentNote.getProject().getProjectId(), Step.valueOf(noteMoveRequestDto.getStep()));
                if (resultList.size() == 0) {
                    currentNote.updatePreviousIdAndNextId(0L, 0L);
                    currentNote.updateStepWhileMoveNote(Step.valueOf(noteMoveRequestDto.getStep()));
                }
            }
        }
        //Step 안 바뀐다면... 죽여줘..
        else {
            // 옮기려고 하는 step에서의 노트 간 연결이 db와 같은지 확인
            if (noteMoveRequestDto.getGoalPreNoteId() != 0L && noteMoveRequestDto.getGoalNextNoteId() != 0L) {
                Note goalPreNote = noteRepository.findById(noteMoveRequestDto.getGoalPreNoteId()).orElseThrow(() -> new ApiRequestException("수정하려는 노트가 존재하지 않음"));
                Note goalNextNote = noteRepository.findById(noteMoveRequestDto.getGoalNextNoteId()).orElseThrow(() -> new ApiRequestException("수정하려는 노트가 존재하지 않음"));
                if (goalPreNote.getNextId().equals(noteMoveRequestDto.getGoalNextNoteId())) {
                    goalPreNote.updatePreviousIdAndNextId(goalPreNote.getPreviousId(), currentNote.getNoteId());
                    currentNote.updatePreviousIdAndNextId(goalPreNote.getNoteId(), goalNextNote.getNoteId());
                    goalNextNote.updatePreviousIdAndNextId(currentNote.getNoteId(), goalNextNote.getNextId());
                } else {
                    throw new ApiRequestException("새로고침 후 시도해주세요.");
                }
            } else if (noteMoveRequestDto.getGoalPreNoteId() == 0L && noteMoveRequestDto.getGoalNextNoteId() != 0L) {
                Note goalNextNote = noteRepository.findById(noteMoveRequestDto.getGoalNextNoteId()).orElseThrow(() -> new ApiRequestException("수정하려는 노트가 존재하지 않음"));
                if (goalNextNote.getPreviousId().equals(0L)) {
                    currentNote.updatePreviousIdAndNextId(0L, goalNextNote.getNoteId());
                    goalNextNote.updatePreviousIdAndNextId(currentNote.getNoteId(), goalNextNote.getNextId());
                } else {
                    throw new ApiRequestException("새로고침 후 시도해주세요.");
                }
            } else if (noteMoveRequestDto.getGoalPreNoteId() != 0L && noteMoveRequestDto.getGoalNextNoteId() == 0L) {
                Note goalPreNote = noteRepository.findById(noteMoveRequestDto.getGoalPreNoteId()).orElseThrow(() -> new ApiRequestException("수정하려는 노트가 존재하지 않음"));
                if (goalPreNote.getNextId().equals(0L)) {
                    goalPreNote.updatePreviousIdAndNextId(goalPreNote.getPreviousId(), currentNote.getNextId());
                    currentNote.updatePreviousIdAndNextId(goalPreNote.getNoteId(), 0L);
                } else {
                    throw new ApiRequestException("새로고침 후 시도해주세요.");
                }
            }
            // TODO 목표 스텝에 아무것도 없다면 아무것도 없는지 확인
            else {
                throw new ApiRequestException("새로고침 후 시도해주세요.");
            }
        }
        return NoteUpdateResponseDto.of(currentNote);
    }

    // Note 작성
    @Transactional
    public NoteCreateResponseDto createNote(Long projectId, NoteCreateRequestDto noteCreateRequestDto, User currentUser) {

        UserProjectMapping userProjectMapping = userProjectMappingRepository
                .findByUserIdAndProjectId(currentUser.getUserId(), projectId)
                .orElseThrow(() -> new ApiRequestException("해당 프로젝트에 소속된 유저가 아닙니다."));

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

        // topNote가 있다면, topNote의 pre와 next 바꿔주고 dtoNote 저장한다.
        if (topNote != null) {
            Note savedNote = noteRepository.save(Note
                    .of(noteCreateRequestDto, deadline, step, user, project, topNote.getNoteId(), 0L));
            topNote.updatePreviousIdAndNextId(topNote.getPreviousId(), savedNote.getNoteId());
            return NoteCreateResponseDto.of(savedNote);
        }
        // topNote 없다면, 그냥 저장한다
        else {
            return NoteCreateResponseDto
                    .of(noteRepository.save(Note
                            .of(noteCreateRequestDto, deadline, step, user, project, 0L, 0L)));
        }
    }

    // 해당 Project 에서 내가 작성한 Note 조회
    public NoteMineInProjectResponseDto readNotesMineOnly(Long projectId, User currentUser, int page, int size) {

        // Project 조회
        projectRepository.findById(projectId).orElseThrow(
                () -> new ApiRequestException("내가 작성한 문서를 조회할 프로젝트가 없습니다.")
        );

        // 해당 Project 에서 내가 작성한 Note 죄회
        List<NoteReadMineEachResponseDto> myNoteList =
                noteRepository.findAllNoteByProjectAndUserOrderByCreatedAtDesc(
                        projectId, currentUser.getUserId(), pandanUtils.dealWithPageRequestParam(page, size))
                        .stream()
                        .map(NoteReadMineEachResponseDto::fromEntity)
                        .collect(Collectors.toList());

        return NoteMineInProjectResponseDto.of(myNoteList);
    }

    // 전체 Project 에서 내가 북마크한 Note 조회
    public NoteBookmarkedResponseDto readBookmarkedMine(User currentUser, int page, int size) {

        // 해당 북마크한 Note 조회
        List<NoteEachBookmarkedResponseDto> noteEachBookmarkedResponseDto =
                bookmarkRepository.findNoteByUserIdInBookmark(
                        currentUser.getUserId(), pandanUtils.dealWithPageRequestParam(page, size));

        return NoteBookmarkedResponseDto.builder().noteList(noteEachBookmarkedResponseDto).build();
    }

    // Note 삭제
    @Transactional
    public NoteDeleteResponseDto deleteNote(Long noteId) {
        // 삭제할 Note 조회
        Note note = noteRepository.findById(noteId).orElseThrow(
                () -> new ApiRequestException("이미 삭제된 노트입니다.")
        );

        // Note에 연관된  코멘트 삭제
        commentRepository.deleteCommentByNoteId(noteId);

        // Note 에 연관된 북마크 삭제
        bookmarkRepository.deleteByNote(noteId);

        Note previousNote = noteRepository.findById(note.getPreviousId()).orElse(null);
        Note nextNote = noteRepository.findById(note.getNextId()).orElse(null);

        try {
            previousNote.updatePreviousIdAndNextId(previousNote.getPreviousId(), nextNote.getNoteId());
        } catch (Exception e) {
            log.info(e.toString());
        }

        try {
            nextNote.updatePreviousIdAndNextId(previousNote.getNoteId(), nextNote.getNextId());
        } catch (Exception e) {
            log.info(e.toString());
        }

        // Note 삭제
        noteRepository.delete(note);

        return NoteDeleteResponseDto.builder()
                .noteId(noteId)
                .build();
    }

    // Note 칸반형 조회 (칸반 페이지)
    @Transactional
    public KanbanNoteSearchResponseDto readKanbanNote(Long projectId) {

        // Project 조회
        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new ApiRequestException("칸반을 조회할 프로젝트가 없습니다.")
        );

        // Project로 전체 노트 리스트 가져오기
        List<Note> rawNoteList = noteRepository.findByProject(project);

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
    public NoteSearchResponseDto readOrdinaryNote(Long projectId, int page, int size) {
        List<OrdinaryNoteEachResponseDto> ordinaryNoteEachResponseDtoList = new ArrayList<>();

        // Project 조회
        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new ApiRequestException("파일을 조회할 프로젝트가 없습니다.")
        );


        for (Note note : noteRepository.findAllByProjectOrderByCreatedAtDesc(
                project, pandanUtils.dealWithPageRequestParam(page, size))) {
            ordinaryNoteEachResponseDtoList.add((OrdinaryNoteEachResponseDto.fromEntity(note)));
        }

        return NoteSearchResponseDto.of(ordinaryNoteEachResponseDtoList);
    }

    // 전체 프로젝트에서 내가 작성한 노트 조회
    public NoteMineInTotalResponseDto readMyNoteInTotalProject(User currentUser, int page, int size) {
        List<NoteEachMineInTotalResponseDto> resultList =
                noteRepository.findUserNoteInTotalProject(
                        currentUser.getUserId(), pandanUtils.dealWithPageRequestParam(page, size));
        return NoteMineInTotalResponseDto.builder().myNoteList(resultList).build();
    }

    // 내가 소속된 프로젝트에서 제목으로 노트 검색
    public NoteSearchInTotalResponseDto searchNoteInMyProjects(User currentUser, String rawKeyword) {
        List<String> keywordList = pandanUtils.parseKeywordToList(rawKeyword);
        List<noteEachSearchInTotalResponseDto> resultList = noteRepository.findNotesByUserIdAndKeywordInTotal(currentUser.getUserId(), keywordList);
        return NoteSearchInTotalResponseDto.builder().noteList(resultList).build();
    }

    // 내가 작성한 문서들 중에서 제목으로 노트 검색
    public NoteSearchInMineResponseDto searchNoteInMyNotes(User currentUser, String rawKeyword) {
        List<String> keywordList = pandanUtils.parseKeywordToList(rawKeyword);
        List<NoteEachSearchInMineResponseDto> resultList = noteRepository.findNotesByUserIdAndKeywordInMine(currentUser.getUserId(), keywordList);
        return NoteSearchInMineResponseDto.builder().noteList(resultList).build();
    }


}
