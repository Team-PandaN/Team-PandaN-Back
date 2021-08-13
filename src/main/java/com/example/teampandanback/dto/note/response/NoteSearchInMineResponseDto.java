package com.example.teampandanback.dto.note.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class NoteSearchInMineResponseDto {
    private List<NoteEachSearchInMineResponseDto> noteList;

    @Builder
    public NoteSearchInMineResponseDto(List<NoteEachSearchInMineResponseDto> noteList) {
        this.noteList = noteList;
    }
}