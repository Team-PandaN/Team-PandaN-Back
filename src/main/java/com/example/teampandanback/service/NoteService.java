package com.example.teampandanback.service;

import com.example.teampandanback.domain.note.Note;
import com.example.teampandanback.domain.note.NoteRepository;
import com.example.teampandanback.domain.note.Step;
import com.example.teampandanback.domain.project.Project;
import com.example.teampandanback.domain.project.ProjectRepository;
import com.example.teampandanback.domain.user.User;
import com.example.teampandanback.domain.user.UserRepository;
import com.example.teampandanback.domain.user_project_mapping.UserProjectMapping;
import com.example.teampandanback.domain.user_project_mapping.UserProjectMappingRepository;
import com.example.teampandanback.domain.user_project_mapping.UserProjectRole;
import com.example.teampandanback.dto.auth.SessionUser;
import com.example.teampandanback.dto.note.request.NoteCreateRequestDto;
import com.example.teampandanback.dto.note.request.NoteRequestDto;
import com.example.teampandanback.dto.note.response.*;
import com.example.teampandanback.exception.ApiRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.mail.Session;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class NoteService {
    private final NoteRepository noteRepository;
    private final UserProjectMappingRepository userProjectMappingRepository;

    public static LocalDate changeType(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateString, formatter);
        return date;
    }

    @Transactional
    public NoteResponseDto readNoteDetail(Long noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ApiRequestException("작성된 노트가 없습니다."));
        return NoteResponseDto.builder()
                .noteId(note.getNoteId())
                .title(note.getTitle())
                .content(note.getContent())
                .deadline(note.getDeadline())
                .build();
    }

    @Transactional
    public NoteResponseDto updateNoteDetail(Long noteId, NoteRequestDto noteRequestDto) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ApiRequestException("수정 할 노트가 없습니다."));

        // #1-1
        note.update(noteRequestDto, changeType(noteRequestDto.getDeadline()));
        // What: noteRequestDto 만 받다가, 형변환 파라미터를 따로 받는 것으로 변경
        // Why: Note.java의 #1에서 수정 사항이 반영되는 부분입니다.
        // How: 형변환된 파라미터를 하나 더 받습니다.

        // #2
        return NoteResponseDto.of(note);
        // What; 빌더 패턴을 직접 쓰는 것에서, 빌더 패턴을 수행하는 of 메소드를 이용하는 것으로 변경
        // Why: Dto 객체를 만드는 빌더가 NoteResponseDto.java 에서 이미 정의되었기 때문에 바꾸었습니다.
        // How: NoteResponseDto.java 에 정의된 of 메소드를 사용하였습니다.
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
                noteResponseDtoList1.add(NoteResponseDto.builder()
                        .noteId(note.getNoteId())
                        .title(note.getTitle())
                        .content(note.getContent())
                        .deadline(note.getDeadline())
                        .build());
            }
        }
        noteOfProjectResponseDtoList.add(NoteOfProjectResponseDto.builder()
                .step(Step.STORAGE)
                .noteResponseDtoList(noteResponseDtoList1)
                .build());

        for (Note note : noteRepository.findNoteByProject_projectId(projectId)) {
            if (note.getStep().equals(Step.TODO)) {
                noteResponseDtoList2.add(NoteResponseDto.builder()
                        .noteId(note.getNoteId())
                        .title(note.getTitle())
                        .content(note.getContent())
                        .deadline(note.getDeadline())
                        .build());
            }
        }
        noteOfProjectResponseDtoList.add(NoteOfProjectResponseDto.builder()
                .step(Step.TODO)
                .noteResponseDtoList(noteResponseDtoList2)
                .build());

        for (Note note : noteRepository.findNoteByProject_projectId(projectId)) {
            if (note.getStep().equals(Step.PROCESSING)) {
                noteResponseDtoList3.add(NoteResponseDto.builder()
                        .noteId(note.getNoteId())
                        .title(note.getTitle())
                        .content(note.getContent())
                        .deadline(note.getDeadline())
                        .build());
            }
        }
        noteOfProjectResponseDtoList.add(NoteOfProjectResponseDto.builder()
                .step(Step.PROCESSING)
                .noteResponseDtoList(noteResponseDtoList3)
                .build());

        for (Note note : noteRepository.findNoteByProject_projectId(projectId)) {
            if (note.getStep().equals(Step.DONE)) {
                noteResponseDtoList4.add(NoteResponseDto.builder()
                        .noteId(note.getNoteId())
                        .title(note.getTitle())
                        .content(note.getContent())
                        .deadline(note.getDeadline())
                        .build());
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
            noteResponseDtoList.add(NoteResponseDto.builder()
                    .noteId(note.getNoteId())
                    .title(note.getTitle())
                    .content(note.getContent())
                    .deadline(note.getDeadline())
                    .build());
        }

        return NoteSearchResponseDto.builder()
                .notes(noteResponseDtoList)
                .build();
    }
}
