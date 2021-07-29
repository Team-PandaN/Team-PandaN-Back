package com.example.teampandanback.dto.note.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class NoteDeleteResponseDto {
    private Long noteId;

    @Builder
    public NoteDeleteResponseDto(Long noteId) {
        this.noteId = noteId;
    }
}
