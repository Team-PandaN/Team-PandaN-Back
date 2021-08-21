package com.example.teampandanback.dto.project.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ProjectDetailForProjectListDto {
    // 프로젝트 목록 조회시 사용되는 프로젝트 정보

    // 프로젝트 id
    private long projectId;

    // 프로젝트 명
    private String title;

    // 프로젝트 설명
    private String detail;

    // 프로젝트 안에 노트 수
    private long noteCount;

    // 프로젝트 안에 노트 중 가장 최근 수정 일자
    private LocalDateTime recentNoteUpdateDate;

    public ProjectDetailForProjectListDto(long projectId, String title, String detail, long noteCount, LocalDateTime recentNoteUpdateDate) {
        this.projectId = projectId;
        this.title = title;
        this.detail = detail;
        this.noteCount = noteCount;
        this.recentNoteUpdateDate = recentNoteUpdateDate;
    }
}