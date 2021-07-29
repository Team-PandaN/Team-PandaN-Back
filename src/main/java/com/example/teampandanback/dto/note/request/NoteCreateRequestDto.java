package com.example.teampandanback.dto.note.request;

import lombok.Getter;

@Getter
public class NoteCreateRequestDto {
    private String title;
    private String content;
    private String deadline;
    private String step;

    // #3
    // What: 노트 작성 용 request를 받기 위한 Dto를 만들었습니다.
    // Why: 노트 생성 시 step 인자가 필요하기 때문입니다.
    // How: Step을 받기 위해 추가로 하나의 변수를 더 만들었습니다.
}
