package com.example.teampandanback.dto.note.response;

import com.example.teampandanback.domain.note.Step;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class NoteOfProjectResponseDto {
    private final Step step;
    private final List<NoteResponseDto> notes;

    @Builder
    public NoteOfProjectResponseDto(Step step, List<NoteResponseDto> noteResponseDtoList) {
        this.step = step;
        this.notes = noteResponseDtoList;
    }

}

