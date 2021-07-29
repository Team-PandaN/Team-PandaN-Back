package com.example.teampandanback.dto.note.response;

import com.example.teampandanback.domain.note.Note;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class NoteResponseDto {
    private Long noteId;
    private String title;
    private String content;
    private LocalDate deadline;

    @Builder
    public NoteResponseDto(Long noteId, String title, String content, LocalDate deadline) {
        this.noteId = noteId;
        this.title = title;
        this.content = content;
        this.deadline = deadline;
    }

    public static NoteResponseDto of (Note note) {
        return NoteResponseDto.builder()
                .noteId(note.getNoteId())
                .title(note.getTitle())
                .content(note.getContent())
                .deadline(note.getDeadline())
                .build();
    }
}
