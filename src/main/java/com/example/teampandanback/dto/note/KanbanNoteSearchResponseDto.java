package com.example.teampandanback.dto.note;


import com.example.teampandanback.domain.note.NoteRepository;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class KanbanNoteSearchResponseDto {
    private List<NoteOfProjectResponseDto> project;

    public KanbanNoteSearchResponseDto(List<NoteOfProjectResponseDto> noteOfProjectResponseDtoList) {
        this.project = noteOfProjectResponseDtoList;
    }

}

