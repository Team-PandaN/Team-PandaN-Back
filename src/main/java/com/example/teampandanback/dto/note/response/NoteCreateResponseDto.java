package com.example.teampandanback.dto.note.response;

import com.example.teampandanback.domain.note.Note;
import com.example.teampandanback.domain.note.Step;
import com.example.teampandanback.dto.file.request.FileDetailRequestDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class NoteCreateResponseDto {

    private Long noteId;
    private String title;
    private String content;
    private String deadline;
    private String step;
    private List<FileDetailRequestDto> files;

    @Builder
    public NoteCreateResponseDto(Long noteId, String title, String content, LocalDate deadline, Step step) {
        this.noteId = noteId;
        this.title = title;
        this.content = content;
        this.deadline = deadline.toString();
        this.step = step.toString();
    }

    public void uploadFile(List<FileDetailRequestDto> files) {
        this.files = files;
    }

    public static NoteCreateResponseDto of(Note note) {
        return NoteCreateResponseDto.builder()
                .noteId(note.getNoteId())
                .title(note.getTitle())
                .content(note.getContent())
                .deadline(note.getDeadline())
                .step(note.getStep())
                .build();
    }
}
