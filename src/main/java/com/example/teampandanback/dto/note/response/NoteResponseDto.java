package com.example.teampandanback.dto.note.response;
// What; dto/note를 dto/note/request와 dto/note/response로 구분했습니다.
// Why: Dto의 수가 많아지면서 요청용 Dto와 응답용 Dto를 구분하기 위함입니다.
// How: Request와 Response를 패키지 이름으로 사용하여 dto를 구분하여 패키지 안에 담았습니다.

import com.example.teampandanback.domain.note.Note;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class NoteResponseDto {
    private Long noteId;
    private String title;
    private String content;
    private LocalDate deadline;

    @Builder
    public NoteResponseDto(Long noteId, String title, String content, LocalDate deadline) {
        this.noteId = noteId;
        this.title = title;
        this.content = content;
        this.deadline = deadline;
    }

    public static NoteResponseDto of (Note note) {
        return NoteResponseDto.builder()
                .noteId(note.getNoteId())
                .title(note.getTitle())
                .content(note.getContent())
                .deadline(note.getDeadline())
                .build();
    }
}
