package com.example.teampandanback.dto.note.response;

import com.example.teampandanback.domain.note.Step;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class NoteOfProjectResponseDto {
    private final String step;
    private final List<KanbanNoteEachResponseDto> notes;

    @Builder
    public NoteOfProjectResponseDto(Step step, List<KanbanNoteEachResponseDto> kanbanNoteEachResponseDtoList) {
        this.step = step.toString();
        this.notes = kanbanNoteEachResponseDtoList;
    }

    public static NoteOfProjectResponseDto of (Step step, List<KanbanNoteEachResponseDto> kanbanNoteEachResponseDtoList) {
        return NoteOfProjectResponseDto.builder()
                .step(step)
                .kanbanNoteEachResponseDtoList(kanbanNoteEachResponseDtoList)
                .build();
    }
}

