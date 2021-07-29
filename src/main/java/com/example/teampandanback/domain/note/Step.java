package com.example.teampandanback.domain.note;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Step {
    STORAGE(1, "창고"),
    TODO(2, "할것"),
    PROCESSING(3, "진행중"),
    DONE(4, "끝");

    private final int id;
    private final String step;

}
