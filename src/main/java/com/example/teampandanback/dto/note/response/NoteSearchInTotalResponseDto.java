package com.example.teampandanback.dto.note.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class NoteSearchInTotalResponseDto {
    private List<noteEachSearchInTotalResponseDto> noteList;

    @Builder
    public NoteSearchInTotalResponseDto(List<noteEachSearchInTotalResponseDto> noteList) {
        this.noteList = noteList;
    }
}