package com.example.teampandanback.dto.note.response;

import com.example.teampandanback.domain.note.Note;
import com.example.teampandanback.domain.note.Step;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class OrdinaryNoteEachResponseDto {
    private Long noteId;
    private String title;
    private String createdAt;
    private String step;

    @Builder
    public OrdinaryNoteEachResponseDto(Long noteId, String title, LocalDateTime createdAt, Step step) {
        this.noteId = noteId;
        this.title = title;
        this.createdAt = createdAt.toString();
        this.step = step.toString();
    }

    public static OrdinaryNoteEachResponseDto fromEntity(Note note){
        return OrdinaryNoteEachResponseDto.builder()
                .title(note.getTitle())
                .noteId(note.getNoteId())
                .step(note.getStep())
                .createdAt(note.getCreatedAt())
                .build();
    }


}
