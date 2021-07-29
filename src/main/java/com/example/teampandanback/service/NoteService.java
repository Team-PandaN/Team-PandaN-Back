package com.example.teampandanback.service;

import com.example.teampandanback.domain.note.Note;
import com.example.teampandanback.domain.note.NoteRepository;
import com.example.teampandanback.dto.note.NoteDeleteResponseDto;
import com.example.teampandanback.dto.note.NoteRequestDto;
import com.example.teampandanback.dto.note.NoteResponseDto;
import com.example.teampandanback.exception.ApiRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
    public NoteResponseDto findNoteDetail(Long noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(()-> new IllegalArgumentException("작성된 노트가 없습니다."));
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
                .orElseThrow(()-> new IllegalArgumentException("수정 할 노트가 없습니다."));
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
        Note note = noteRepository.save(Note.of(noteRequestDto, deadline));
        return NoteResponseDto.of(note);
    }

    @Transactional
    public NoteDeleteResponseDto deleteNote(Long noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(()-> new IllegalArgumentException("삭제 할 노트가 없습니다."));
        noteRepository.delete(note);
        return NoteDeleteResponseDto.builder()
                .noteId(note.getNoteId())
                .build();
    }
}
