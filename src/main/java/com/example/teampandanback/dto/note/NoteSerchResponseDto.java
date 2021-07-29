package com.example.teampandanback.dto.note;

import lombok.Builder;

import java.util.List;

public class NoteSerchResponseDto {
    private final List<NoteResponseDto> noteResponseDtoList;

    @Builder
    public NoteSerchResponseDto(List<NoteResponseDto> noteResponseDtoList) {
        this.noteResponseDtoList = noteResponseDtoList;
    }
}
