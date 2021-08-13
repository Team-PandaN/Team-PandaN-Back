package com.example.teampandanback.dto.note.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class NoteSearchInBookmarkResponseDto {
    private List<NoteEachSearchInBookmarkResponseDto> noteList;

    @Builder
    public NoteSearchInBookmarkResponseDto(List<NoteEachSearchInBookmarkResponseDto> noteList) {
        this.noteList = noteList;
    }
}