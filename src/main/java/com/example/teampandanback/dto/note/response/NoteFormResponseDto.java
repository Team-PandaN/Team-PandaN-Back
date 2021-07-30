package com.example.teampandanback.dto.note.response;

import com.example.teampandanback.domain.note.Note;
import com.example.teampandanback.domain.note.Step;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class NoteFormResponseDto {

    private Long noteId;
    private String title;
    private String content;
    private String deadline;
    private String step;

    @Builder
    public NoteFormResponseDto(Long noteId, String title, String content, LocalDate deadline, Step step) {
        this.noteId = noteId;
        this.title = title;
        this.content = content;
        this.deadline = deadline.toString();
        this.step = step.toString();
    }

    public static NoteFormResponseDto of (Note note) {
        return NoteFormResponseDto.builder()
                .noteId(note.getNoteId())
                .title(note.getTitle())
                .content(note.getContent())
                .deadline(note.getDeadline())
                .step(note.getStep())
                .build();
    }
}
