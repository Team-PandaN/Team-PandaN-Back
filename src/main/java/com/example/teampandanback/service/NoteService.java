package com.example.teampandanback.service;

import com.example.teampandanback.domain.note.Note;
import com.example.teampandanback.domain.note.NoteRepository;
import com.example.teampandanback.dto.note.NoteDeleteResponseDto;
import com.example.teampandanback.dto.note.NoteRequestDto;
import com.example.teampandanback.dto.note.NoteResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NoteService {

    private final NoteRepository noteRepository;


    public NoteResponseDto findNoteDetail(Long noteId) {
        Note note = noteRepository.findByNoteId(noteId);
        return NoteResponseDto.builder()
                .noteId(note.getNoteId())
                .title(note.getTitle())
                .content(note.getContent())
                .deadline(note.getDeadline())
                .build();
    }


    public NoteResponseDto updateNoteDetail(Long noteId,NoteRequestDto noteRequestDto) {
        Note note = noteRepository.findByNoteId(noteId);
        note.update(noteRequestDto);
        noteRepository.save(note);

        return NoteResponseDto.builder()
                .noteId(note.getNoteId())
                .title(note.getTitle())
                .content(note.getContent())
                .deadline(note.getDeadline())
                .build();
    }


    public NoteDeleteResponseDto deleteNote(Long noteId) {
        Note note = noteRepository.findByNoteId(noteId);
        noteRepository.delete(note);
        return NoteDeleteResponseDto.builder()
                .noteId(note.getNoteId())
                .build();
    }
}
