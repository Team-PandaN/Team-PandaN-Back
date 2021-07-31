package com.example.teampandanback.dto.note.request;

import lombok.Getter;

@Getter
public class NoteUpdateRequestDto {
    private String title;
    private String content;
    private String deadline;
    private String step;
}
