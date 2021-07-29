package com.example.teampandanback.dto.note.response;

import com.example.teampandanback.domain.note.Note;
import com.example.teampandanback.domain.note.Step;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class NoteCreateResponseDto {

    // #5
    // What: 노트 작성 용 response를 보내기 위한 Dto를 만들었는데,
    //       LocalDate를 제외하고는 String 으로 선언했습니다.
    // Why:  1. 노트 생성 시 step 인자를 돌려줄 필요가 있기 때문입니다.
    //       2. 분명 LocalDate는 그대로 필요하실 것이고, 나머지는 그냥 String으로 받으면 그만일 것입니다.
    //          다만 한 번 프론트와 이야기는 해보면 좋겠습니다.
    // How: Step step 추가했습니다.

    private String noteId;
    private String title;
    private String content;
    private LocalDate deadline;
    private String step;

    @Builder
    public NoteCreateResponseDto(Long noteId, String title, String content, LocalDate deadline, Step step) {
        this.noteId = noteId.toString();
        this.title = title;
        this.content = content;
        this.deadline = deadline;
        this.step = step.toString();
    }

    public static NoteCreateResponseDto of (Note note) {
        return NoteCreateResponseDto.builder()
                .noteId(note.getNoteId())
                .title(note.getTitle())
                .content(note.getContent())
                .deadline(note.getDeadline())
                .step(note.getStep())
                .build();
    }
}
