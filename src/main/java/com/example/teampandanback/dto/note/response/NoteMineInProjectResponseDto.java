package com.example.teampandanback.dto.note.response;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
public class NoteMineInProjectResponseDto {
    private List<NoteReadMineEachResponseDto> myNoteList;

    @Builder
    public NoteMineInProjectResponseDto(List<NoteReadMineEachResponseDto> myNoteList) {
        this.myNoteList = myNoteList;
    }

    public static NoteMineInProjectResponseDto of (List<NoteReadMineEachResponseDto> myNoteList) {
        return NoteMineInProjectResponseDto.builder()
                .myNoteList(myNoteList)
                .build();
    }
}
