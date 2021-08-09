package com.example.teampandanback.service;

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
import com.example.teampandanback.dto.auth.SessionUser;
import com.example.teampandanback.dto.note.request.NoteCreateRequestDto;
import com.example.teampandanback.dto.note.request.NoteUpdateRequestDto;
import com.example.teampandanback.dto.note.response.*;
import com.example.teampandanback.exception.ApiRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    // String 자료형으로 받은 날짜를 LocalDate 자료형으로 형변환
    private LocalDate changeType(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateString, formatter);
        return date;
    }

    // Note 상세 조회
    @Transactional
    public NoteResponseDto readNoteDetail(Long noteId, SessionUser sessionUser) {
        NoteResponseDto noteResponseDto = noteRepository.findByNoteId(noteId)
                .orElseThrow(() -> new ApiRequestException("작성된 노트가 없습니다."));

        Optional<Bookmark> bookmark = bookmarkRepository.findByUserIdAndNoteId(sessionUser.getUserId(), noteId);
        noteResponseDto.setBookmark(bookmark.isPresent());
        return noteResponseDto;
    }

    // Note 업데이트
    @Transactional
    public NoteUpdateResponseDto updateNoteDetail(Long noteId, NoteUpdateRequestDto noteUpdateRequestDto) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ApiRequestException("수정 할 노트가 없습니다."));

        note.update(noteUpdateRequestDto, changeType(noteUpdateRequestDto.getDeadline()), Step.valueOf(noteUpdateRequestDto.getStep()));

        return NoteUpdateResponseDto.of(note);
    }

    // Note 작성
    @Transactional
    public NoteCreateResponseDto createNote(Long projectId, NoteCreateRequestDto noteCreateRequestDto, SessionUser sessionUser) {

        UserProjectMapping userProjectMapping =
                userProjectMappingRepository
                        .findByUser_UserIdAndProject_ProjectId(sessionUser.getUserId(), projectId);

        if(userProjectMapping == null){
            throw new ApiRequestException("노트를 작성할 수 없습니다.");
        }

        // [노트 생성] 전달받은 String deadline을 LocalDate 자료형으로 형변환
        LocalDate deadline = changeType(noteCreateRequestDto.getDeadline());

        // [노트 생성] 전달받은 String step을 Enum Step으로
        Step step = Step.valueOf(noteCreateRequestDto.getStep());

        // [노트 생성] 찾은 userProjectMappingRepository를 통해 user와 프로젝트 가져오기
        User user = userProjectMapping.getUser();
        Project project = userProjectMapping.getProject();

        // [노트 생성] 전달받은 noteCreateRequestDto를 Note.java에 정의한 of 메소드에 전달하여 빌더 패턴에 넣는다.
        Note note = noteRepository.save(Note.of(noteCreateRequestDto, deadline, step, user, project));
        return NoteCreateResponseDto.of(note);
    }

    // 해당 Project 에서 내가 작성한 Note 조회
    public NoteMineInProjectResponseDto readNotesMineOnly(Long projectId, SessionUser sessionUser) {

        // Project 조회
        projectRepository.findById(projectId).orElseThrow(
                () -> new ApiRequestException("내가 작성한 문서를 조회할 프로젝트가 없습니다.")
        );

        // 해당 Project 에서 내가 작성한 Note 죄회
        List<NoteResponseDto> myNoteList = noteRepository.findByProjectAndUser(projectId, sessionUser.getUserId())
                .stream()
                .map(NoteResponseDto::of)
                .collect(Collectors.toList());

        return NoteMineInProjectResponseDto.of(myNoteList);
    }

    // 전체 Project 에서 내가 북마크한 Note 조회
    public NoteBookmarkedResponseDto readBookmarkedMine(SessionUser sessionUser) {

        // 해당 북마크한 Note 조회
        List<NoteEachBookmarkedResponseDto> noteEachBookmarkedResponseDto =
                bookmarkRepository.findByUserId(sessionUser.getUserId());

        return NoteBookmarkedResponseDto.builder().noteList(noteEachBookmarkedResponseDto).build();
    }

    // Note 삭제
    @Transactional
    public NoteDeleteResponseDto deleteNote(Long noteId) {
        // 삭제할 Note 조회
        Note note = noteRepository.findById(noteId).orElseThrow(
                () -> new ApiRequestException("이미 삭제된 노트입니다.")
        );

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
        List<NoteResponseDto> noteResponseDtoList1 = new ArrayList<>();
        List<NoteResponseDto> noteResponseDtoList2 = new ArrayList<>();
        List<NoteResponseDto> noteResponseDtoList3 = new ArrayList<>();
        List<NoteResponseDto> noteResponseDtoList4 = new ArrayList<>();

        // Project 조회
        Project project = projectRepository.findById(projectId).orElseThrow(
                ()-> new ApiRequestException("칸반을 조회할 프로젝트가 없습니다.")
        );

        for (Note note : noteRepository.findByProject(project)) {
            switch(note.getStep()){
                case STORAGE:
                    noteResponseDtoList1.add((NoteResponseDto.of(note))); break;
                case TODO:
                    noteResponseDtoList2.add((NoteResponseDto.of(note))); break;
                case PROCESSING:
                    noteResponseDtoList3.add((NoteResponseDto.of(note))); break;
                case DONE:
                    noteResponseDtoList4.add(NoteResponseDto.of(note)); break;
            }
        }
        // Note 를 각 상태별로 List 로 묶어서 응답 보내기
        noteOfProjectResponseDtoList.add(NoteOfProjectResponseDto.of(Step.STORAGE, noteResponseDtoList1));
        noteOfProjectResponseDtoList.add(NoteOfProjectResponseDto.of(Step.TODO, noteResponseDtoList2));
        noteOfProjectResponseDtoList.add(NoteOfProjectResponseDto.of(Step.PROCESSING, noteResponseDtoList3));
        noteOfProjectResponseDtoList.add(NoteOfProjectResponseDto.of(Step.DONE, noteResponseDtoList4));

        return KanbanNoteSearchResponseDto.builder()
                .noteOfProjectResponseDtoList(noteOfProjectResponseDtoList)
                .build();
    }

    // Note 일반형 조회 (파일 페이지)
    @Transactional
    public NoteSearchResponseDto readOrdinaryNote(Long projectId) {
        List<NoteResponseDto> noteResponseDtoList = new ArrayList<>();

        // Project 조회
        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new ApiRequestException("파일을 조회할 프로젝트가 없습니다.")
        );

        for (Note note : noteRepository.findByProject(project)) {
            noteResponseDtoList.add((NoteResponseDto.of(note)));
        }

        return NoteSearchResponseDto.of(noteResponseDtoList);
    }
}
