package com.example.teampandanback.controller;

import com.example.teampandanback.dto.project.ProejctRequestDto;
import com.example.teampandanback.dto.project.ProjectDeleteResponseDto;
import com.example.teampandanback.dto.project.ProjectListResponseDto;
import com.example.teampandanback.dto.project.ProjectResponseDto;
import com.example.teampandanback.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/projects")
@RestController
public class ProjectController {
    private final ProjectService projectService;

    // Project 목록 조회
    @GetMapping("")
    public ProjectListResponseDto readProjectList(){
        return ProjectListResponseDto.builder()
                .projectResponseDtoList(projectService.readProjectList())
                .build();
    }

    // Project 생성
    @PostMapping("")
    public ProjectResponseDto createProject(@RequestBody ProejctRequestDto requestDto){
        return projectService.createProject(requestDto);
    }

    // Project 수정
    @PutMapping("/{projectId}")
    public ProjectResponseDto updateProject(@PathVariable("projectId") Long projectId,
                                            @RequestBody ProejctRequestDto requestDto){
        return projectService.updateProject(projectId, requestDto);
    }

    // Project 삭제
    @DeleteMapping("/{projectId}")
    public ProjectDeleteResponseDto deleteProject(@PathVariable("projectId") Long projectId){
        return projectService.deleteProject(projectId);
    }


}
