package com.example.teampandanback.dto.file.response;

import com.example.teampandanback.dto.note.response.NoteResponseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class FileUpdateResponseDto {
    private NoteResponseDto detail;
    private List<FileDetailResponseDto> files;

    @Builder
    public FileUpdateResponseDto(NoteResponseDto noteResponseDto, List<FileDetailResponseDto> files) {
        this.detail = noteResponseDto;
        this.files = files;
    }

    public static FileUpdateResponseDto fromEntity(NoteResponseDto noteResponseDto, List<FileDetailResponseDto> files) {
        return FileUpdateResponseDto.builder()
                .noteResponseDto(noteResponseDto)
                .files(files)
                .build();
    }

}

