package com.example.teampandanback.dto.note.request;

import lombok.Getter;

@Getter
public class NoteMoveRequestDto {
    private String step;
    private Long fromPreNoteId;
    private Long fromNextNoteId;
    private Long toPreNoteId;
    private Long toNextNodeId;
}
