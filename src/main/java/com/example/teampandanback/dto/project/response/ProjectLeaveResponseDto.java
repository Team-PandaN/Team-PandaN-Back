package com.example.teampandanback.dto.project.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ProjectLeaveResponseDto {
    // 프로젝트 탈퇴시 반환하는 Dto

    // 탈퇴한 프로젝트 ID
    private Long projectId;

    @Builder
    public ProjectLeaveResponseDto(Long projectId) {
        this.projectId = projectId;
    }
}