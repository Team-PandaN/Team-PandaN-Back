package com.example.teampandanback.dto.note.request;

import lombok.Getter;

@Getter
public class NoteMoveRequestDto {
    private String title;
    private String content;
    private String deadline;
    private String step;
    private Long fromPreNoteId;
    private Long fromNextNoteId;
    private Long toPreNoteId;
    private Long toNextNodeId;
}
