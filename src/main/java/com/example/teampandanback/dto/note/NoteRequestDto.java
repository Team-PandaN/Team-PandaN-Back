package com.example.teampandanback.dto.note;

import lombok.Getter;

@Getter
public class NoteCreateRequestDto {
    private String title;
    private String content;
    private String deadline;
}
