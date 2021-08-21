package com.example.teampandanback.dto.file.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FileDeleteResponseDto {
    private Long fileId;

    @Builder
    public FileDeleteResponseDto(Long fileId) {
        this.fileId = fileId;
    }
}
