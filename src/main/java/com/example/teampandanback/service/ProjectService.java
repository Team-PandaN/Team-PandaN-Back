package com.example.teampandanback.service;

import com.example.teampandanback.domain.project.Project;
import com.example.teampandanback.domain.project.ProjectRepository;
import com.example.teampandanback.dto.project.ProejctRequestDto;
import com.example.teampandanback.dto.project.ProjectDeleteResponseDto;
import com.example.teampandanback.dto.project.ProjectResponseDto;
import com.example.teampandanback.exception.ApiRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    // Project 목록 조회
    public List<ProjectResponseDto> readProjectList(){
         return projectRepository.findAll()
                 .stream().map(ProjectResponseDto::of).collect(Collectors.toList());
    }

    // Project 생성
    public ProjectResponseDto createProject(ProejctRequestDto requestDto){
        Project project = projectRepository.save(Project.of(requestDto));

        return ProjectResponseDto.of(project);
    }

    // Project 수정
    public ProjectResponseDto updateProject(Long projectId, ProejctRequestDto requestDto){
        Project project = projectRepository.findById(projectId)
                .orElseThrow(()-> new ApiRequestException("수정할 프로젝트가 없습니다."));
        project.update(requestDto);

        return ProjectResponseDto.of(project);
    }

    // Project 삭제
    public ProjectDeleteResponseDto deleteProject(Long projectId){
        projectRepository.deleteById(projectId);

        return ProjectDeleteResponseDto.builder()
                .projectId(projectId)
                .build();
    }

}
