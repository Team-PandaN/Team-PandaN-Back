package com.example.teampandanback.dto.note.request;

import com.example.teampandanback.dto.file.request.FileUpdateDetailRequestDto;
import lombok.Getter;

import java.util.List;

@Getter
public class NoteUpdateRequestDto {
    private String title;
    private String content;
    private String deadline;
    private List<FileUpdateDetailRequestDto> files;
}
