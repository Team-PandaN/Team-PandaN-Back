package com.example.teampandanback.dto.note.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class NoteSearchResponseDto {
    private final List<NoteResponseDto> notes;

    @Builder
    public NoteSearchResponseDto(List<NoteResponseDto> notes) {
        this.notes = notes;
    }


    public static NoteSearchResponseDto of (List<NoteResponseDto> noteResponseDtoList) {
        return NoteSearchResponseDto.builder()
                .notes(noteResponseDtoList)
                .build();
    }
}
