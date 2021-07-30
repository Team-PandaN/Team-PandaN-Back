package com.example.teampandanback.controller;

import com.example.teampandanback.config.auth.LoginUser;
import com.example.teampandanback.dto.auth.SessionUser;
import com.example.teampandanback.dto.project.*;
import com.example.teampandanback.service.EncryptService;
import com.example.teampandanback.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/projects")
@RestController
public class ProjectController {

    private final EncryptService encryptService;
    private final ProjectService projectService;

    @GetMapping("/{projectId}/invites")
    public ResponseEntity<StringEncryptResponseDto> findTagByCategoryId(@PathVariable("projectId") Long projectId) {
        log.info(">>> Encrypting >>>");
        return ResponseEntity.ok().body(encryptService.encodeString(projectId));
    }

    // Project 목록 조회
    @GetMapping("")
    public ProjectListResponseDto readProjectList(@LoginUser SessionUser sessionUser) {
        return ProjectListResponseDto.builder()
                .projectResponseDtoList(projectService.readProjectList(sessionUser))
                .build();
    }

    // Project 생성
    @PostMapping("")
    public ProjectResponseDto createProject(@RequestBody ProjectRequestDto requestDto, @LoginUser SessionUser sessionUser) {
        return projectService.createProject(requestDto, sessionUser);
    }

    // Project 수정
    @PutMapping("/{projectId}")
    public ProjectResponseDto updateProject(@PathVariable("projectId") Long projectId,
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


}
