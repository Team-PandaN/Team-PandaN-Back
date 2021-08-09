package com.example.teampandanback.dto.note.response;

import com.example.teampandanback.domain.note.Note;
import com.example.teampandanback.domain.note.Step;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class NoteResponseDto {
    private Long noteId;
    private String title;
    private String content;
    private String deadline;
    private String step;
    public Boolean isBookmark;
    private Long projectId;
    private String projectTitle;
    private String writer;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public NoteResponseDto(Long noteId, String title, String content, LocalDate deadline, Step step,
                           Long projectId, String projectTitle, String writer, LocalDateTime createdAt, LocalDateTime modifiedAt)
    {
        this.noteId = noteId;
        this.title = title;
        this.content = content;
        this.deadline = deadline.toString();
        this.step = step.toString();
        this.projectId = projectId;
        this.projectTitle = projectTitle;
        this.writer = writer;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public void setBookmark(Boolean b){
        this.isBookmark = b;
    }

    @Builder
    public NoteResponseDto(Long noteId, String title, String content, LocalDate deadline, Step step) {
        this.noteId = noteId;
        this.title = title;
        this.content = content;
        this.deadline = deadline.toString();
        this.step = step.toString();
    }

    public static NoteResponseDto of (Note note) {
        return NoteResponseDto.builder()
                .noteId(note.getNoteId())
                .title(note.getTitle())
                .content(note.getContent())
                .deadline(note.getDeadline())
                .step(note.getStep())
                .build();
    }
}
