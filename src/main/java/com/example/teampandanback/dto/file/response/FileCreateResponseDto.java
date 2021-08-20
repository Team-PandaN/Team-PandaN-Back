package com.example.teampandanback.dto.file.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class FileCreateResponseDto {
    private List<FileDetailResponseDto> files;

    @Builder
    public FileCreateResponseDto(List<FileDetailResponseDto> files) {
        this.files = files;
    }


}