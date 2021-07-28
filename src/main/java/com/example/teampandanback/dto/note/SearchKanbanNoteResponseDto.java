package com.example.teampandanback.dto.note;


import lombok.Getter;

import java.util.List;

@Getter
public class SearchKanbanNoteResponseDto {
    private List<ProjectResponseDto> project;
    private boolean error;

    public SearchKanbanNoteResponseDto(List<ProjectResponseDto> project, boolean error) {
        this.project = project;
        this.error = error;
    }
}
