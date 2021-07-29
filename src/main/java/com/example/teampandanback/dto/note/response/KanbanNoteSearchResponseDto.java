package com.example.teampandanback.dto.note.response;


import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class KanbanNoteSearchResponseDto {
    private List<NoteOfProjectResponseDto> projects;

    @Builder
    public KanbanNoteSearchResponseDto(List<NoteOfProjectResponseDto> noteOfProjectResponseDtoList) {
        this.projects = noteOfProjectResponseDtoList;
    }
}

