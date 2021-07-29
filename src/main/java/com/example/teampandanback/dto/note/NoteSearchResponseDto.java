package com.example.teampandanback.dto.note;

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
}
