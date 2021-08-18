package com.example.teampandanback.dto.note.request;

import lombok.Getter;

@Getter
public class NoteMoveRequestDto {
    private String title;
    private String content;
    private String deadline;
    private String step;
    private Long originPreNoteId;
    private Long originNextNoteId;
    private Long goalPreNoteId;
    private Long goalNextNoteId;
}
