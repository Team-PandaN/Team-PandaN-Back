package com.example.teampandanback.dto.note.response;

import com.example.teampandanback.dto.file.response.FileDetailResponseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class NoteDetailResponseDto {
    private NoteResponseDto detail;
    private List<FileDetailResponseDto> files;

    @Builder
    public NoteDetailResponseDto(NoteResponseDto noteResponseDto, List<FileDetailResponseDto> files) {
        this.detail = noteResponseDto;
        this.files = files;
    }

    public static NoteDetailResponseDto fromEntity(NoteResponseDto noteResponseDto, List<FileDetailResponseDto> files) {
        return NoteDetailResponseDto.builder()
                .noteResponseDto(noteResponseDto)
                .files(files)
                .build();
    }
}
