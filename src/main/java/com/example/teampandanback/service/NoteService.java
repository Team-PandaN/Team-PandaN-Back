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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        if (!currentNote.getPrevious().equals(noteMoveRequestDto.getOriginPreNoteId()) || !currentNote.getNext().equals(noteMoveRequestDto.getOriginNextNoteId())) {
            throw new ApiRequestException("새로고침 후 시도해주세요");
        }

        // Step이 바뀐다면
        if (!currentNote.getStep().toString().equals(noteMoveRequestDto.getStep())) {
            // 옮기려고 하는 step에서의 노트 간 연결이 db와 같은지 확인
            if (noteMoveRequestDto.getGoalPreNoteId() != 0L && noteMoveRequestDto.getGoalNextNoteId() != 0L) {
                Note goalPreNote = noteRepository.findById(noteMoveRequestDto.getGoalPreNoteId()).orElseThrow(() -> new ApiRequestException("수정하려는 노트가 존재하지 않음"));
                Note goalNextNote = noteRepository.findById(noteMoveRequestDto.getGoalNextNoteId()).orElseThrow(() -> new ApiRequestException("수정하려는 노트가 존재하지 않음"));
                if (goalPreNote.getNext().equals(noteMoveRequestDto.getGoalNextNoteId())) {
                    goalPreNote.updateWhileMoveNote(goalPreNote.getPrevious(), currentNote.getNoteId());
                    currentNote.updateWhileMoveNote(goalPreNote.getNoteId(), goalNextNote.getNoteId());
                    currentNote.updateStepWhileMoveNote(goalPreNote.getStep());
                    goalNextNote.updateWhileMoveNote(currentNote.getNoteId(), goalNextNote.getNext());
                } else {
                    throw new ApiRequestException("새로고침 후 시도해주세요.");
                }
            } else if (noteMoveRequestDto.getGoalPreNoteId() == 0L && noteMoveRequestDto.getGoalNextNoteId() != 0L) {
                Note goalNextNote = noteRepository.findById(noteMoveRequestDto.getGoalNextNoteId()).orElseThrow(() -> new ApiRequestException("수정하려는 노트가 존재하지 않음"));
                if (goalNextNote.getPrevious().equals(0L)) {
                    currentNote.updateWhileMoveNote(0L, goalNextNote.getNoteId());
                    currentNote.updateStepWhileMoveNote(goalNextNote.getStep());
                    goalNextNote.updateWhileMoveNote(currentNote.getNoteId(), goalNextNote.getNext());
                } else {
                    throw new ApiRequestException("새로고침 후 시도해주세요.");
                }
            } else if (noteMoveRequestDto.getGoalPreNoteId() != 0L && noteMoveRequestDto.getGoalNextNoteId() == 0L) {
                Note goalPreNote = noteRepository.findById(noteMoveRequestDto.getGoalPreNoteId()).orElseThrow(() -> new ApiRequestException("수정하려는 노트가 존재하지 않음"));
                if (goalPreNote.getNext().equals(0L)) {
                    goalPreNote.updateWhileMoveNote(goalPreNote.getPrevious(), currentNote.getNext());
                    currentNote.updateWhileMoveNote(goalPreNote.getNoteId(), 0L);
                    currentNote.updateStepWhileMoveNote(goalPreNote.getStep());
                } else {
                    throw new ApiRequestException("새로고침 후 시도해주세요.");
                }
            }
            // TODO 목표 스텝에 아무것도 없다면 아무것도 없는지 확인
            else {
                List<Note> resultList = noteRepository.findAllByProjectAndStep(currentNote.getProject().getProjectId(), Step.valueOf(noteMoveRequestDto.getStep()));
                if (resultList.size() == 0) {
                    currentNote.updateWhileMoveNote(0L, 0L);
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
                if (goalPreNote.getNext().equals(noteMoveRequestDto.getGoalNextNoteId())) {
                    goalPreNote.updateWhileMoveNote(goalPreNote.getPrevious(), currentNote.getNoteId());
                    currentNote.updateWhileMoveNote(goalPreNote.getNoteId(), goalNextNote.getNoteId());
                    goalNextNote.updateWhileMoveNote(currentNote.getNoteId(), goalNextNote.getNext());
                } else {
                    throw new ApiRequestException("새로고침 후 시도해주세요.");
                }
            } else if (noteMoveRequestDto.getGoalPreNoteId() == 0L && noteMoveRequestDto.getGoalNextNoteId() != 0L) {
                Note goalNextNote = noteRepository.findById(noteMoveRequestDto.getGoalNextNoteId()).orElseThrow(() -> new ApiRequestException("수정하려는 노트가 존재하지 않음"));
                if (goalNextNote.getPrevious().equals(0L)) {
                    currentNote.updateWhileMoveNote(0L, goalNextNote.getNoteId());
                    goalNextNote.updateWhileMoveNote(currentNote.getNoteId(), goalNextNote.getNext());
                } else {
                    throw new ApiRequestException("새로고침 후 시도해주세요.");
                }
            } else if (noteMoveRequestDto.getGoalPreNoteId() != 0L && noteMoveRequestDto.getGoalNextNoteId() == 0L) {
                Note goalPreNote = noteRepository.findById(noteMoveRequestDto.getGoalPreNoteId()).orElseThrow(() -> new ApiRequestException("수정하려는 노트가 존재하지 않음"));
                if (goalPreNote.getNext().equals(0L)) {
                    goalPreNote.updateWhileMoveNote(goalPreNote.getPrevious(), currentNote.getNext());
                    currentNote.updateWhileMoveNote(goalPreNote.getNoteId(), 0L);
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

        // [노트 생성] 전달받은 String deadline을 LocalDate 자료형으로 형변환
        LocalDate deadline = pandanUtils.changeType(noteCreateRequestDto.getDeadline());
        // [노트 생성] 전달받은 String step을 Enum Step으로
        Step step = Step.valueOf(noteCreateRequestDto.getStep());
        // [노트 생성] 찾은 userProjectMappingRepository를 통해 user와 프로젝트 가져오기
        User user = userProjectMapping.getUser();
        Project project = userProjectMapping.getProject();

        // 현재 프로젝트의 해당 스텝에 있는 노트 리스트를 가져온 후 가장 마지막 노트를 찾는다.
        // 조건을 만족하지 않는다면 lastNote 값에 그 다음의 if문 조건절 비교를 위해 null 넣어준다.
        Note lastNote = noteRepository
                .findAllByProjectAndStep(projectId, step)
                .stream()
                .filter(each -> each.getNext().equals(0L))
                .findFirst().orElse(null);

        // lastNote 있고 noteCreateRequestDto previous 값과 일치한다면
        if (lastNote != null && lastNote.getNoteId().equals(noteCreateRequestDto.getPreviousNoteId())) {
            // [노트 생성] 전달받은 noteCreateRequestDto를 Note.java에 정의한 of 메소드에 전달하여 빌더 패턴에 넣는다.
            Note note = noteRepository
                    .save(Note.of(noteCreateRequestDto, deadline, step, user, project, lastNote.getNoteId(), 0L));
            // lastNote의 Next 값을 현재 생성된 노트의 ID로 변경해준다.
            lastNote.updateWhileCreate(note.getNoteId());
            return NoteCreateResponseDto.of(note);
        }
        // lastNote 없고 noteCreateRequestDto previous 값도 0이 맞다면
        else if (lastNote == null && noteCreateRequestDto.getPreviousNoteId() == 0) {
            // lastNote 가 없으므로 바로 저장한다.
            return NoteCreateResponseDto.of(noteRepository.save(Note.of(noteCreateRequestDto, deadline, step, user, project, 0L, 0L)));
        }
        // lastNote 없는데 noteCreateRequestDto prvious 값은 0이 아니거나 다른 값이라면
        // lastNote 있는데 previous 값은 0 내지는 값이 다르다면
        // 사용자의 칸반이 새로고침 되어야.. 싱크 맞지 않는 것 -> 웹소켓 문제로 이어진다.
        else {
            throw new ApiRequestException("새로고침 이후 다시 시도해주세요.");
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
                        projectId, currentUser.getUserId(), PandanUtils.dealWithPageRequestParam(page, size))
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
                        currentUser.getUserId(), PandanUtils.dealWithPageRequestParam(page, size));

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

        // Note 삭제
        noteRepository.delete(note);

        return NoteDeleteResponseDto.builder()
                .noteId(noteId)
                .build();
    }

    // Note 칸반형 조회 (칸반 페이지)
    @Transactional
    public KanbanNoteSearchResponseDto readKanbanNote(Long projectId) {
        List<NoteOfProjectResponseDto> noteOfProjectResponseDtoList = new ArrayList<>();
        List<KanbanNoteEachResponseDto> kanbanNoteEachResponseDtoList1 = new ArrayList<>();
        List<KanbanNoteEachResponseDto> kanbanNoteEachResponseDtoList2 = new ArrayList<>();
        List<KanbanNoteEachResponseDto> kanbanNoteEachResponseDtoList3 = new ArrayList<>();
        List<KanbanNoteEachResponseDto> kanbanNoteEachResponseDtoList4 = new ArrayList<>();

        // Project 조회
        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new ApiRequestException("칸반을 조회할 프로젝트가 없습니다.")
        );

        for (Note note : noteRepository.findByProject(project)) {
            switch (note.getStep()) {
                case STORAGE:
                    kanbanNoteEachResponseDtoList1.add((KanbanNoteEachResponseDto.of(note)));
                    break;
                case TODO:
                    kanbanNoteEachResponseDtoList2.add((KanbanNoteEachResponseDto.of(note)));
                    break;
                case PROCESSING:
                    kanbanNoteEachResponseDtoList3.add((KanbanNoteEachResponseDto.of(note)));
                    break;
                case DONE:
                    kanbanNoteEachResponseDtoList4.add(KanbanNoteEachResponseDto.of(note));
                    break;
            }
        }
        // Note 를 각 상태별로 List 로 묶어서 응답 보내기
        noteOfProjectResponseDtoList.add(NoteOfProjectResponseDto.of(Step.STORAGE, kanbanNoteEachResponseDtoList1));
        noteOfProjectResponseDtoList.add(NoteOfProjectResponseDto.of(Step.TODO, kanbanNoteEachResponseDtoList2));
        noteOfProjectResponseDtoList.add(NoteOfProjectResponseDto.of(Step.PROCESSING, kanbanNoteEachResponseDtoList3));
        noteOfProjectResponseDtoList.add(NoteOfProjectResponseDto.of(Step.DONE, kanbanNoteEachResponseDtoList4));

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
                project, PandanUtils.dealWithPageRequestParam(page, size))) {
            ordinaryNoteEachResponseDtoList.add((OrdinaryNoteEachResponseDto.fromEntity(note)));
        }

        return NoteSearchResponseDto.of(ordinaryNoteEachResponseDtoList);
    }

    // 전체 프로젝트에서 내가 작성한 노트 조회
    public NoteMineInTotalResponseDto readMyNoteInTotalProject(User currentUser, int page, int size) {
        List<NoteEachMineInTotalResponseDto> resultList =
                noteRepository.findUserNoteInTotalProject(
                        currentUser.getUserId(), PandanUtils.dealWithPageRequestParam(page, size));
        return NoteMineInTotalResponseDto.builder().myNoteList(resultList).build();
    }

    public NoteSearchInTotalResponseDto searchNoteInMyProjects(User currentUser, String rawKeyword) {
        List<String> keywordList = pandanUtils.parseKeywordToList(rawKeyword);
        List<noteEachSearchInTotalResponseDto> resultList = noteRepository.findNotesByUserIdAndKeywordInTotal(currentUser.getUserId(), keywordList);
        return NoteSearchInTotalResponseDto.builder().noteList(resultList).build();
    }

    public NoteSearchInMineResponseDto searchNoteInMyNotes(User currentUser, String rawKeyword) {
        List<String> keywordList = pandanUtils.parseKeywordToList(rawKeyword);
        List<NoteEachSearchInMineResponseDto> resultList = noteRepository.findNotesByUserIdAndKeywordInMine(currentUser.getUserId(), keywordList);
        return NoteSearchInMineResponseDto.builder().noteList(resultList).build();
    }


}
