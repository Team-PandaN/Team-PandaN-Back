package com.example.teampandanback.dto.note.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class NoteBookmarkedResponseDto {
    private List<NoteEachBookmarkedResponseDto> noteList;

    @Builder
    public NoteBookmarkedResponseDto(List<NoteEachBookmarkedResponseDto> noteList){
        this.noteList = noteList;
    }

}
