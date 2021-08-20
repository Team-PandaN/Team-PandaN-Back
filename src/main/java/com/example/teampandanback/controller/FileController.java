package com.example.teampandanback.controller;


import com.example.teampandanback.OAuth2.UserDetailsImpl;
import com.example.teampandanback.dto.file.request.FileCreateRequestDto;
import com.example.teampandanback.dto.file.request.FileUpdateRequestDto;
import com.example.teampandanback.dto.file.response.FileCreateResponseDto;
import com.example.teampandanback.dto.file.response.FileDeleteResponseDto;
import com.example.teampandanback.dto.file.response.FileUpdateResponseDto;
import com.example.teampandanback.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"파일"})
@RequiredArgsConstructor
@RestController
public class FileController {

    private final FileService fileService;

    @ApiOperation(value = "파일 작성")
    @PostMapping("/api/files/{noteId}")
    public FileCreateResponseDto createFile(
            @PathVariable Long noteId, @AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody FileCreateRequestDto fileCreateRequestDto) {
        return fileService.createFile(noteId, userDetails.getUser(), fileCreateRequestDto);
    }

    @ApiOperation(value = "파일 수정")
    @PutMapping("/api/files/{fileId}")
    public FileUpdateResponseDto updateFile(@PathVariable Long fileId, @AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody FileUpdateRequestDto fileUpdateRequestDto) {
        return fileService.updateFile(fileId, userDetails.getUser(), fileUpdateRequestDto);
    }

    @ApiOperation(value = "파일 삭제")
    @DeleteMapping("/api/files/{fileId}")
    public FileDeleteResponseDto deleteNote(@PathVariable Long fileId,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return fileService.deleteFile(fileId, userDetails.getUser());
    }

}
