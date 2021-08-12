package com.example.teampandanback.controller;

import com.example.teampandanback.config.auth.LoginUser;
import com.example.teampandanback.dto.auth.SessionUser;
import com.example.teampandanback.dto.project.request.ProjectInvitedRequestDto;
import com.example.teampandanback.dto.project.request.ProjectRequestDto;
import com.example.teampandanback.dto.project.request.ProjectResponseDto;
import com.example.teampandanback.dto.project.response.*;
import com.example.teampandanback.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/projects")
@RestController
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/{projectId}/invites")
    public ProjectInviteResponseDto invite(@PathVariable("projectId") Long projectId, @LoginUser SessionUser sessionUser){
        return projectService.inviteProject(projectId, sessionUser);
    }

    // Project 목록 조회
    @GetMapping("")
    public ProjectListResponseDto readProjectList(@LoginUser SessionUser sessionUser) {
        return ProjectListResponseDto.builder()
                .userProjectMappingDtoList(projectService.readProjectList(sessionUser))
                .build();
    }

    // 사이드 바에 들어갈 Project 목록 조회(최대 5개)
    @GetMapping("/sidebar")
    public ProjectListSidebarResponseDto readProjectListSidebar(@LoginUser SessionUser sessionUser) {
        return ProjectListSidebarResponseDto.builder()
                .projectSidebarResponseList(projectService.readProjectListSidebar(sessionUser))
                .build();
    }


    // Project 상세 조회
    @GetMapping("/{projectId}")
    public ProjectDetailResponseDto readProjectDetail(@LoginUser SessionUser sessionUser, @PathVariable Long projectId){
        return projectService.readProjectDetail(sessionUser, projectId);
    }

    // Project 생성
    @PostMapping("")
    public ProjectResponseDto createProject(@RequestBody ProjectRequestDto requestDto, @LoginUser SessionUser sessionUser) {
        return projectService.createProject(requestDto, sessionUser);
    }

    // Project 수정
    @PutMapping("/{projectId}")
    public ProjectDetailResponseDto updateProject(@PathVariable("projectId") Long projectId,
                                            @RequestBody ProjectRequestDto requestDto,
                                            @LoginUser SessionUser sessionUser) {
        return projectService.updateProject(projectId, requestDto, sessionUser);
    }

    // Project 삭제
    @DeleteMapping("/{projectId}")
    public ProjectDeleteResponseDto deleteProject(@PathVariable("projectId") Long projectId,
                                                  @LoginUser SessionUser sessionUser) {
        return projectService.deleteProject(projectId, sessionUser);
    }

    // Project 회원 조회
    @GetMapping("/{projectId}/crews")
    public ProjectCrewResponseDto readCrewList(@PathVariable("projectId") Long projectId){
        return projectService.readCrewList(projectId);
    }

    //프로젝트 참여
    @PostMapping("/invites")
    public ProjectInvitedResponseDto invited(@RequestBody ProjectInvitedRequestDto projectInvitedRequestDto,
                                             @LoginUser SessionUser sessionUser){
        return projectService.invitedProject(projectInvitedRequestDto,sessionUser);
    }


}