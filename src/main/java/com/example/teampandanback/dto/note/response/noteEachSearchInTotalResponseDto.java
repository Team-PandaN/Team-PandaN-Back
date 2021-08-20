package com.example.teampandanback.dto.note.response;

import com.example.teampandanback.domain.note.Step;

import lombok.Builder;
import lombok.Getter;
import org.apache.tomcat.jni.Local;


import java.time.LocalDateTime;

@Getter
public class noteEachSearchInTotalResponseDto {
    private Long noteId;
    private String title;
    private Step step;
    private Long projectId;
    private String projectTitle;
    private String writer;
    private LocalDateTime createdAt;

    @Builder
    public noteEachSearchInTotalResponseDto(Long noteId, String title, Step step, Long projectId, String projectTitle, String writer, LocalDateTime createdAt) {
        this.noteId = noteId;
        this.title = title;
        this.step = step;
        this.projectId = projectId;
        this.projectTitle = projectTitle;
        this.writer = writer;
        this.createdAt = createdAt;
    }
}