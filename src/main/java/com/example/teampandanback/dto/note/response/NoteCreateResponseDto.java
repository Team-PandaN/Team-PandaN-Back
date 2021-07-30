package com.example.teampandanback.dto.note.response;

import com.example.teampandanback.domain.note.Note;
import com.example.teampandanback.domain.note.Step;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class NoteCreateResponseDto {

    private String noteId;
    private String title;
    private String content;
    private LocalDate deadline;
    private String step;

    @Builder
    public NoteCreateResponseDto(Long noteId, String title, String content, LocalDate deadline, Step step) {
        this.noteId = noteId.toString();
        this.title = title;
        this.content = content;
        this.deadline = deadline;
        this.step = step.toString();
    }

    public static NoteCreateResponseDto of (Note note) {
        return NoteCreateResponseDto.builder()
                .noteId(note.getNoteId())
                .title(note.getTitle())
                .content(note.getContent())
                .deadline(note.getDeadline())
                .step(note.getStep())
                .build();
    }
}
