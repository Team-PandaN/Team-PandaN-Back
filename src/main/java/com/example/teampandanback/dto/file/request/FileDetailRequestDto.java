package com.example.teampandanback.dto.file.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FileDetailRequestDto {

    private String fileName;
    private String fileUrl;

    @Builder
    public FileDetailRequestDto(String fileName, String fileUrl) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }
}
