package com.example.teampandanback.dto.note.request;

import lombok.Getter;

@Getter
public class NoteCreateRequestDto {
    private String title;
    private String content;
    private String deadline;
    private String step;
    private List<FileDetailRequestDto> files;

    @Builder
    public NoteCreateRequestDto(String title, String content, String deadline, String step) {
        this.title = title;
        this.content = content;
        this.deadline = deadline;
        this.step = step;
    }
}
