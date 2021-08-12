package com.example.teampandanback.dto.note.search;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class NoteSearchInTotalResponse {
    private List<NoteEachSearchInTotalResponse> noteList;

    @Builder
    public NoteSearchInTotalResponse(List<NoteEachSearchInTotalResponse> noteList) {
        this.noteList = noteList;
    }
}