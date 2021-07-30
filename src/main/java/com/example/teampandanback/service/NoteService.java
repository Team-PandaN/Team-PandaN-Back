package com.example.teampandanback.service;

import com.example.teampandanback.domain.note.Note;
import com.example.teampandanback.domain.note.NoteRepository;
import com.example.teampandanback.domain.note.Step;
import com.example.teampandanback.domain.project.Project;
import com.example.teampandanback.domain.user.User;
import com.example.teampandanback.domain.user_project_mapping.UserProjectMapping;
import com.example.teampandanback.domain.user_project_mapping.UserProjectMappingRepository;
import com.example.teampandanback.dto.auth.SessionUser;
import com.example.teampandanback.dto.note.request.NoteFromRequestDto;
import com.example.teampandanback.dto.note.response.*;
import com.example.teampandanback.exception.ApiRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class NoteService {
    private final NoteRepository noteRepository;
    private final UserProjectMappingRepository userProjectMappingRepository;

    private LocalDate changeType(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateString, formatter);
        return date;
    }

    @Transactional
    public NoteResponseDto readNoteDetail(Long noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ApiRequestException("작성된 노트가 없습니다."));
        return NoteResponseDto.of(note);
    }

    @Transactional
    public NoteFormResponseDto updateNoteDetail(Long noteId, NoteFromRequestDto noteFromRequestDto) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ApiRequestException("수정 할 노트가 없습니다."));

        // #1-1
        note.update(noteFromRequestDto, changeType(noteFromRequestDto.getDeadline()),Step.valueOf(noteFromRequestDto.getStep()));

        // #2
        return NoteFormResponseDto.of(note);
    }

    @Transactional
    public NoteFormResponseDto createNote(Long projectId, NoteFromRequestDto noteFromRequestDto, SessionUser sessionUser) {

        UserProjectMapping userProjectMapping =
                userProjectMappingRepository
                        .findByUser_UserIdAndProject_ProjectId(sessionUser.getUserId(), projectId);

        // [노트 생성] 전달받은 String deadline을 LocalDate 자료형으로 형변환
        LocalDate deadline = changeType(noteFromRequestDto.getDeadline());

        // [노트 생성] 전달받은 String step을 Enum Step으로
        Step step = Step.valueOf(noteFromRequestDto.getStep());

        // [노트 생성] 찾은 userProjectMappingRepository를 통해 user와 프로젝트 가져오기
        User user = userProjectMapping.getUser();
        Project project = userProjectMapping.getProject();

        // [노트 생성] 전달받은 noteCreateRequestDto를 Note.java에 정의한 of 메소드에 전달하여 빌더 패턴에 넣는다.
        Note note = noteRepository.save(Note.of(noteFromRequestDto, deadline, step, user, project));
        return NoteFormResponseDto.of(note);
    }

    public NoteMineOnlyResponseDto readNotesMineOnly(Long projectId, SessionUser sessionUser){
        List<Note> noteList = noteRepository.findNoteByProject_projectId(projectId);
        List<NoteResponseDto> myNoteList = noteList
                .stream()
                .filter(note -> note.getUser().getUserId().equals(sessionUser.getUserId()))
                .map(NoteResponseDto::of)
                .collect(Collectors.toList());

        return NoteMineOnlyResponseDto.of(myNoteList);
    }


    @Transactional
    public NoteDeleteResponseDto deleteNote(Long noteId) {
        noteRepository.deleteById(noteId);
        return NoteDeleteResponseDto.builder()
                .noteId(noteId)
                .build();
    }

    @Transactional
    public KanbanNoteSearchResponseDto readKanbanNote(Long projectId) {
        List<NoteOfProjectResponseDto> noteOfProjectResponseDtoList = new ArrayList<>();
        List<NoteResponseDto> noteResponseDtoList1 = new ArrayList<>();
        List<NoteResponseDto> noteResponseDtoList2 = new ArrayList<>();
        List<NoteResponseDto> noteResponseDtoList3 = new ArrayList<>();
        List<NoteResponseDto> noteResponseDtoList4 = new ArrayList<>();

        for (Note note : noteRepository.findNoteByProject_projectId(projectId)) {
            if (note.getStep().equals(Step.STORAGE)) {
                noteResponseDtoList1.add((NoteResponseDto.of(note)));
            }
        }
        noteOfProjectResponseDtoList.add(NoteOfProjectResponseDto.builder()
                .step(Step.STORAGE)
                .noteResponseDtoList(noteResponseDtoList1)
                .build());

        for (Note note : noteRepository.findNoteByProject_projectId(projectId)) {
            if (note.getStep().equals(Step.TODO)) {
                noteResponseDtoList2.add((NoteResponseDto.of(note)));
            }
        }
        noteOfProjectResponseDtoList.add(NoteOfProjectResponseDto.builder()
                .step(Step.TODO)
                .noteResponseDtoList(noteResponseDtoList2)
                .build());

        for (Note note : noteRepository.findNoteByProject_projectId(projectId)) {
            if (note.getStep().equals(Step.PROCESSING)) {
                noteResponseDtoList3.add((NoteResponseDto.of(note)));
            }
        }
        noteOfProjectResponseDtoList.add(NoteOfProjectResponseDto.builder()
                .step(Step.PROCESSING)
                .noteResponseDtoList(noteResponseDtoList3)
                .build());

        for (Note note : noteRepository.findNoteByProject_projectId(projectId)) {
            if (note.getStep().equals(Step.DONE)) {
                noteResponseDtoList4.add(NoteResponseDto.of(note));
            }
        }
        noteOfProjectResponseDtoList.add(NoteOfProjectResponseDto.builder()
                .step(Step.DONE)
                .noteResponseDtoList(noteResponseDtoList4)
                .build());

        return KanbanNoteSearchResponseDto.builder()
                .noteOfProjectResponseDtoList(noteOfProjectResponseDtoList)
                .build();
    }

    @Transactional
    public NoteSearchResponseDto readOrdinaryNote(Long projectId) {
        List<NoteResponseDto> noteResponseDtoList = new ArrayList<>();

        for (Note note : noteRepository.findNoteByProject_projectId(projectId)) {
            noteResponseDtoList.add((NoteResponseDto.of(note)));
        }

        return NoteSearchResponseDto.of(noteResponseDtoList);
    }
}
