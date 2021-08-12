package com.example.teampandanback.dto.project.request;

import com.example.teampandanback.domain.project.Project;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProjectResponseDto {
    private Long projectId;
    private String title;
    private String detail;

    @Builder
    public ProjectResponseDto (Long projectId, String title, String detail) {
        this.projectId = projectId;
        this.title = title;
        this.detail = detail;
    }

    public static ProjectResponseDto fromEntity (Project project) {
        return ProjectResponseDto.builder()
                .title(project.getTitle())
                .detail(project.getDetail())
                .projectId(project.getProjectId())
                .build();
    }

}
