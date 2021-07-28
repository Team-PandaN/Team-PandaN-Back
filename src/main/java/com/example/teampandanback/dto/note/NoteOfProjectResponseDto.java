package com.example.teampandanback.dto.note;

import com.example.teampandanback.domain.note.Step;
import lombok.Getter;

import java.util.List;

@Getter
public class NoteOfProjectResponseDto {
    private final Step step;
    private final List<NoteResponseDto> notes;

    public NoteOfProjectResponseDto(Step step, List<NoteResponseDto> notes) {
        this.step = step;
        this.notes = notes;
    }

}

