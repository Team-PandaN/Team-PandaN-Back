package com.example.teampandanback.dto.project.response;

import com.example.teampandanback.dto.project.request.ProjectResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ProjectListResponseDto {
    List<ProjectEachResponseDTO> projects;

    @Builder
    public ProjectListResponseDto(List<ProjectEachResponseDTO> userProjectMappingDtoList) {
        this.projects = userProjectMappingDtoList;
    }
}
