package com.example.teampandanback.dto.file.response;

import com.example.teampandanback.domain.file.File;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FileDetailResponseDto {

    private Long fileId;
    private String fileName;
    private String fileUrl;

    @Builder
    public FileDetailResponseDto(Long fileId, String fileName, String fileUrl) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }

    public static FileDetailResponseDto fromEntity(File file) {
        return FileDetailResponseDto.builder()
                .fileId(file.getFileId())
                .fileName(file.getFileName())
                .fileUrl(file.getFileUrl())
                .build();
    }
}
