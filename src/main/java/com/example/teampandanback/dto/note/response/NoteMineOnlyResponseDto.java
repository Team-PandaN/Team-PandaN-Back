package com.example.teampandanback.dto.note.response;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
public class NoteMineOnlyResponseDto{
    private List<NoteResponseDto> myNoteList;

    @Builder
    public NoteMineOnlyResponseDto(List<NoteResponseDto> myNoteList) {
        this.myNoteList = myNoteList;
    }

    public static NoteMineOnlyResponseDto of (List<NoteResponseDto> myNoteList) {
        return NoteMineOnlyResponseDto.builder()
                .myNoteList(myNoteList)
                .build();
    }
}
