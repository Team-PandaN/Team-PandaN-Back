package com.example.teampandanback.service;

import com.example.teampandanback.domain.note.Note;
import com.example.teampandanback.domain.note.NoteRepository;
import com.example.teampandanback.domain.note.Step;
import com.example.teampandanback.dto.note.*;
import com.example.teampandanback.exception.ApiRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public static LocalDate changeType (String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateString, formatter);
        return date;
    }

    @Transactional
    public NoteResponseDto readNoteDetail(Long noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(()-> new ApiRequestException("작성된 노트가 없습니다."));
        return NoteResponseDto.builder()
                .noteId(note.getNoteId())
                .title(note.getTitle())
                .content(note.getContent())
                .deadline(note.getDeadline())
                .build();
    }

    @Transactional
    public NoteResponseDto updateNoteDetail(Long noteId,NoteRequestDto noteRequestDto) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(()-> new ApiRequestException("수정 할 노트가 없습니다."));
        note.update(noteRequestDto);
        return NoteResponseDto.builder()
                .noteId(note.getNoteId())
                .title(note.getTitle())
                .content(note.getContent())
                .deadline(note.getDeadline())
                .build();
    }

    @Transactional
    public NoteResponseDto createNote(Long projectId, NoteRequestDto noteRequestDto){
        LocalDate deadline = changeType(noteRequestDto.getDeadline());
        Note notwraw = Note.of(noteRequestDto, deadline);
        Note note = noteRepository.save(Note.of(noteRequestDto, deadline));
        return NoteResponseDto.of(note);
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

        for (Note note : noteRepository.findNoteByProject_projectId(projectId)){
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

        for (Note note : noteRepository.findNoteByProject_projectId(projectId)){
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

        for (Note note : noteRepository.findNoteByProject_projectId(projectId)){
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

        for (Note note : noteRepository.findNoteByProject_projectId(projectId)){
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
}
