package com.example.teampandanback.dto.note.response;

import com.example.teampandanback.domain.note.Note;
import com.example.teampandanback.domain.note.Step;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class NoteUpdateResponseDto {
    private Long noteId;
    private String title;
    private String content;
    private String deadline;
    private String step;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @Builder
    public NoteUpdateResponseDto(Long noteId, String title, String content, LocalDate deadline, Step step,
                                 LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.noteId = noteId;
        this.title = title;
        this.content = content;
        this.deadline = deadline.toString();
        this.step = step.toString();
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static NoteUpdateResponseDto of (Note note) {
        return NoteUpdateResponseDto.builder()
                .noteId(note.getNoteId())
                .title(note.getTitle())
                .content(note.getContent())
                .deadline(note.getDeadline())
                .step(note.getStep())
                .createdAt(note.getCreatedAt())
                .modifiedAt(note.getModifiedAt())
                .build();
    }
}
