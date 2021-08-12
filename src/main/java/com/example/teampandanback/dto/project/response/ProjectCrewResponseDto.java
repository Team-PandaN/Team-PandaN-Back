package com.example.teampandanback.dto.project.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ProjectCrewResponseDto {
    private Long projectId;
    private List<CrewResponseDto> crews;

    @Builder
    public ProjectCrewResponseDto(Long projectId, List<CrewResponseDto> crews) {
        this.projectId = projectId;
        this.crews = crews;
    }
}
