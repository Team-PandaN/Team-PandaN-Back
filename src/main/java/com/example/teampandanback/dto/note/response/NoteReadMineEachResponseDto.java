package com.example.teampandanback.dto.note.response;

import com.example.teampandanback.domain.note.Note;
import com.example.teampandanback.domain.note.Step;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class NoteReadMineEachResponseDto {
    private Long noteId;
    private String title;
    private String createdAt;
    private String step;

    @Builder
    public NoteReadMineEachResponseDto(Long noteId, String title, LocalDateTime createdAt, Step step) {
        this.noteId = noteId;
        this.title = title;
        this.createdAt = createdAt.toString();
        this.step = step.toString();
    }

    public static NoteReadMineEachResponseDto fromEntity(Note note){
        return NoteReadMineEachResponseDto.builder()
                .title(note.getTitle())
                .noteId(note.getNoteId())
                .createdAt(note.getCreatedAt())
                .step(note.getStep())
                .build();
    }


}
