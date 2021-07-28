package com.example.teampandanback.dto.note;

import lombok.Getter;

@Getter

public class NoteResponseDto {
    private Long noteId;
    private String title;
    private String content;
    private String deadline;
}
