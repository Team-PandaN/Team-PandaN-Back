package com.example.teampandanback.controller;

import com.example.teampandanback.dto.project.StringEncryptResponseDto;
import com.example.teampandanback.service.EncryptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/projects")
@RestController
public class ProjectController {

    private final EncryptService encryptService;

    @GetMapping("/{projectId}/invites")
    public ResponseEntity<StringEncryptResponseDto> findTagByCategoryId(@PathVariable("projectId") Long projectId ) {
        log.info(">>> Encrypting >>>");
        return ResponseEntity.ok().body(encryptService.encodeString(projectId));
    }
}