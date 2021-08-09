package com.example.teampandanback.dto.note.response;

import com.example.teampandanback.domain.note.Step;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class NoteEachMineInTotalResponseDto {
    private Long noteId;
    private String title;
    private LocalDateTime createdAt;
    private Step step;
    private Long projectId;
    private String projectTitle;

    public NoteEachMineInTotalResponseDto(Long noteId, String title, LocalDateTime createdAt, Step step, Long projectId, String projectTitle) {
        this.noteId = noteId;
        this.title = title;
        this.createdAt = createdAt;
        this.step = step;
        this.projectId = projectId;
        this.projectTitle = projectTitle;
    }
}
