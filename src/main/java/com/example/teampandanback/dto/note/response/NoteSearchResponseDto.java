package com.example.teampandanback.dto.note.response;
// What; dto/note를 dto/note/request와 dto/note/response로 구분했습니다.
// Why: Dto의 수가 많아지면서 요청용 Dto와 응답용 Dto를 구분하기 위함입니다.
// How: Request와 Response를 패키지 이름으로 사용하여 dto를 구분하여 패키지 안에 담았습니다.

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class NoteSearchResponseDto {
    private final List<NoteResponseDto> notes;

    @Builder
    public NoteSearchResponseDto(List<NoteResponseDto> notes) {
        this.notes = notes;
    }
}
