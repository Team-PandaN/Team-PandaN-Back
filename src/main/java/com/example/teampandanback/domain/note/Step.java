package com.example.teampandanback.domain.note;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Step {
    STORAGE(0, "창고"),
    TODO(1, "할것"),
    PROCESSING(2, "진행중"),
    DONE(3, "끝");

    private final int id;
    private final String step;

}
