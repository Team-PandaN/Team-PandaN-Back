package com.example.teampandanback.domain.note;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MoveStatus {
    UNIQUE(0),
    CURRENTTOP(1),
    CURRENTBOTTOM(2),
    CURRENTBETWEEN(3);

    private final int id;
}
