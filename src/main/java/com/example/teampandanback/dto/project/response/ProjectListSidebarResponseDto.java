package com.example.teampandanback.dto.project.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ProjectListSidebarResponseDto {
    List<ProjectSidebarResponseDto> projects;

    @Builder
    public ProjectListSidebarResponseDto(List<ProjectSidebarResponseDto> projectSidebarResponseList) {
        this.projects = projectSidebarResponseList;
    }
}
