package com.example.teampandanback.dto.note.search;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class NoteSearchInBookmarkResponse {
    private List<NoteEachSearchInBookmarkResponse> noteList;

    @Builder
    public NoteSearchInBookmarkResponse(List<NoteEachSearchInBookmarkResponse> noteList) {
        this.noteList = noteList;
    }
}