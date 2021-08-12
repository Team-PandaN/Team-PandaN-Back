package com.example.teampandanback.dto.project.response;

import com.example.teampandanback.dto.project.request.ProjectResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ProjectListResponseDto {
    List<ProjectResponseDto> projects;

    @Builder
    public ProjectListResponseDto(List<ProjectResponseDto> userProjectMappingDtoList) {
        this.projects = userProjectMappingDtoList;
    }
}
