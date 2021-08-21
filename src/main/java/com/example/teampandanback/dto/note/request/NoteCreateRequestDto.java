package com.example.teampandanback.dto.note.request;

import com.example.teampandanback.dto.file.request.FileDetailRequestDto;
import lombok.Getter;

import java.util.List;

@Getter
public class NoteCreateRequestDto {
    private String title;
    private String content;
    private String deadline;
    private String step;
    private List<FileDetailRequestDto> files;
}
