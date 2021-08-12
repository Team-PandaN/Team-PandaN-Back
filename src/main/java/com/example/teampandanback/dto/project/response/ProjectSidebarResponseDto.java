package com.example.teampandanback.dto.project.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ProjectSidebarResponseDto {
    private long projectId;
    private String title;

    public ProjectSidebarResponseDto(long projectId, String title) {
        this.projectId = projectId;
        this.title = title;
    }
}
