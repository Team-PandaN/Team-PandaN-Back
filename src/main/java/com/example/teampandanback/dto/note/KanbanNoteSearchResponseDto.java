package com.example.teampandanback.dto.note;


import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class KanbanNoteSearchResponseDto {
    private List<NoteOfProjectResponseDto> project;

    @Builder
    public KanbanNoteSearchResponseDto(List<NoteOfProjectResponseDto> noteOfProjectResponseDtoList) {
        this.project = noteOfProjectResponseDtoList;
    }

}

