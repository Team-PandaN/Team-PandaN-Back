package com.example.teampandanback.dto.note.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class NoteMineInTotalResponseDto {
    List<NoteEachMineInTotalResponseDto> myNoteList;

    @Builder
    public NoteMineInTotalResponseDto(List<NoteEachMineInTotalResponseDto> myNoteList) {
        this.myNoteList = myNoteList;
    }
}
