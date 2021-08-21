package com.example.teampandanback.dto.file.response;

import com.example.teampandanback.dto.note.response.NoteResponseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class FileUpdateResponseDto {
    private List<FileDetailResponseDto> files;

    @Builder
    public FileUpdateResponseDto(List<FileDetailResponseDto> files) {
        this.files = files;
    }

    public static FileUpdateResponseDto fromEntity(List<FileDetailResponseDto> files) {
        return FileUpdateResponseDto.builder()
                .files(files)
                .build();
    }

}

