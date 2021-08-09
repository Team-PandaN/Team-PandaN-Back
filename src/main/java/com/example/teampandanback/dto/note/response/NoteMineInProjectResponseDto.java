package com.example.teampandanback.dto.note.response;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
public class NoteMineInProjectResponseDto {
    private List<NoteResponseDto> myNoteList;

    @Builder
    public NoteMineInProjectResponseDto(List<NoteResponseDto> myNoteList) {
        this.myNoteList = myNoteList;
    }

    public static NoteMineInProjectResponseDto of (List<NoteResponseDto> myNoteList) {
        return NoteMineInProjectResponseDto.builder()
                .myNoteList(myNoteList)
                .build();
    }
}
