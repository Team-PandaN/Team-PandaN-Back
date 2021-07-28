package com.example.teampandanback.dto.project;

import com.example.teampandanback.domain.project.Project;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProjectResponseDto {
    private Long projectId;
    private String title;
    private String detail;

    @Builder
    public ProjectResponseDto (String title, String detail, Long projectId) {
        this.title = title;
        this.detail = detail;
        this.projectId = projectId;
    }

    public static ProjectResponseDto of (Project project) {
        return ProjectResponseDto.builder()
                .title(project.getTitle())
                .detail(project.getDetail())
                .projectId(project.getProjectId())
                .build();
    }

}
