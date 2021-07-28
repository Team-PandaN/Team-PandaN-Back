package com.example.teampandanback.dto.note;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NoteResponseDto {
    private Long noteId;
    private String title;
    private String content;
    private LocalDateTime deadline;

    @Builder
    public NoteResponseDto(Long noteId, String title, String content, LocalDateTime deadline) {
        this.noteId = noteId;
        this.title = title;
        this.content = content;
        this.deadline = deadline;
    }
}
