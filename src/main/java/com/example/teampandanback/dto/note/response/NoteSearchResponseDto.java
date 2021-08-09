package com.example.teampandanback.dto.note.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class NoteSearchResponseDto {
    private final List<OrdinaryNoteEachResponseDto> notes;

    @Builder
    public NoteSearchResponseDto(List<OrdinaryNoteEachResponseDto> notes) {
        this.notes = notes;
    }


    public static NoteSearchResponseDto of (List<OrdinaryNoteEachResponseDto> noteResponseDtoList) {
        return NoteSearchResponseDto.builder()
                .notes(noteResponseDtoList)
                .build();
    }
}
