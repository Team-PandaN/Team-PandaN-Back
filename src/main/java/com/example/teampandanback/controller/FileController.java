package com.example.teampandanback.controller;


import com.example.teampandanback.OAuth2.UserDetailsImpl;
import com.example.teampandanback.dto.file.response.FileDeleteResponseDto;
import com.example.teampandanback.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"파일"})
@RequiredArgsConstructor
@RestController
public class FileController {

    private final FileService fileService;

    @ApiOperation(value = "파일 삭제")
    @DeleteMapping("/api/files/{fileId}")
    public FileDeleteResponseDto deleteNote(@PathVariable Long fileId,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return fileService.deleteFile(fileId, userDetails.getUser());
    }

}
