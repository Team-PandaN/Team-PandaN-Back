package com.example.teampandanback.dto.note.response;

import com.example.teampandanback.domain.note.Step;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NoteEachSearchInMineResponseDto {
    private Long noteId;
    private String title;
    private Step step;
    private Long projectId;
    private String projectTitle;
    private LocalDateTime createdAt;

    @Builder
    public NoteEachSearchInMineResponseDto(Long noteId, String title, Step step, Long projectId, String projectTitle, LocalDateTime createdAt) {
        this.noteId = noteId;
        this.title = title;
        this.step = step;
        this.projectId = projectId;
        this.projectTitle = projectTitle;
        this.createdAt = createdAt;
    }
}