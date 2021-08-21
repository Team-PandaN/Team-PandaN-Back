package com.example.teampandanback.dto.note.request;

import com.example.teampandanback.dto.file.request.FileDetailRequestDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class NoteCreateRequestDto {
    private String title;
    private String content;
    private String deadline;
    private String step;
    private List<FileDetailRequestDto> files;

    @Builder
    public NoteCreateRequestDto(String title, String content, String deadline, String step, List<FileDetailRequestDto> files) {
        this.title = title;
        this.content = content;
        this.deadline = deadline;
        this.step = step;
        this.files = files;
    }
}
