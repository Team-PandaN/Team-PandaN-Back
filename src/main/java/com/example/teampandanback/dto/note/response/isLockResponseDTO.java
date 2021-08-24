package com.example.teampandanback.dto.note.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class isLockResponseDTO {
    private final Boolean sameUser;
    private final String writer;
    private final Boolean locked;

    @Builder
    public isLockResponseDTO(Boolean sameUser, String writer, Boolean locked) {
        this.sameUser = sameUser;
        this.writer = writer;
        this.locked = locked;
    }
}
