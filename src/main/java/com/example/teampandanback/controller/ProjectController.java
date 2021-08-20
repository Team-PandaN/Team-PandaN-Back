package com.example.teampandanback.controller;

import com.example.teampandanback.OAuth2.UserDetailsImpl;
import com.example.teampandanback.dto.project.request.ProjectInvitedRequestDto;
import com.example.teampandanback.dto.project.request.ProjectRequestDto;
import com.example.teampandanback.dto.project.request.ProjectResponseDto;
import com.example.teampandanback.dto.project.response.*;
import com.example.teampandanback.service.ProjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@Api(tags = {"프로젝트"})
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/projects")
@RestController
public class ProjectController {

    private final ProjectService projectService;

    @ApiOperation(value = "프로젝트 초대코드 발급")
    @GetMapping("/{projectId}/invites")
    public ProjectInviteResponseDto invite(@PathVariable("projectId") Long projectId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return projectService.inviteProject(projectId, userDetails.getUser());
    }

    // Project 목록 조회
    @ApiOperation(value = "프로젝트 목록 조회")
    @GetMapping("")
    public ProjectListResponseDto readProjectList(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ProjectListResponseDto.builder()
                .userProjectMappingDtoList(projectService.readProjectList(userDetails.getUser()))
                .build();
    }

    // 사이드 바에 들어갈 Project 목록 조회(최대 5개)
    @ApiOperation(value = "사이드 바에 들어갈 Project 목록 조회(최대 5개)")
    @GetMapping("/sidebar")
    public ProjectListSidebarResponseDto readProjectListSidebar(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ProjectListSidebarResponseDto.builder()
                .projectSidebarResponseList(projectService.readProjectListSidebar(userDetails.getUser()))
                .build();
    }


    // Project 상세 조회
    @ApiOperation(value = "프로젝트 상세 조회")
    @GetMapping("/{projectId}")
    public ProjectDetailResponseDto readProjectDetail(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long projectId){
        return projectService.readProjectDetail(userDetails.getUser(), projectId);
    }

    // Project 생성
    @ApiOperation(value = "프로젝트 생성")
    @PostMapping("")
    public ProjectResponseDto createProject(@RequestBody ProjectRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return projectService.createProject(requestDto, userDetails.getUser());
    }

    // Project 수정
    @ApiOperation(value = "프로젝트 수정")
    @PutMapping("/{projectId}")
    public ProjectDetailResponseDto updateProject(@PathVariable("projectId") Long projectId,
                                            @RequestBody ProjectRequestDto requestDto,
                                                  @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return projectService.updateProject(projectId, requestDto, userDetails.getUser());
    }

    // Project 삭제
    @ApiOperation(value = "프로젝트 삭제")
    @DeleteMapping("/{projectId}")
    public ProjectDeleteResponseDto deleteProject(@PathVariable("projectId") Long projectId,
                                                  @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return projectService.deleteProject(projectId, userDetails.getUser());
    }

    // Project 회원 조회
    @ApiOperation(value = "프로젝트 회원 조회" , notes = "누구누구가 들어있는지 볼 수 있음")
    @GetMapping("/{projectId}/crews")
    public ProjectCrewResponseDto readCrewList(@PathVariable("projectId") Long projectId){
        return projectService.readCrewList(projectId);
    }

    //프로젝트 참여
    @ApiOperation(value = "프로젝트 참여" , notes = "회원초대코드로 참여하게 됨")
    @PostMapping("/invites")
    public ProjectInvitedResponseDto invited(@RequestBody ProjectInvitedRequestDto projectInvitedRequestDto,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails){
        return projectService.invitedProject(projectInvitedRequestDto,userDetails.getUser());
    }

    //프로젝트 탈퇴
//    @ApiOperation(value = "프로젝트 탈퇴")
//    @PostMapping("/un-invite/{projectId}")
//    public void unInvite(@PathVariable("projectId") Long projectId, @AuthenticationPrincipal UserDetailsImpl userDetails){
//        projectService.unInviteProject(projectId,userDetails.getUser());
//    }


}