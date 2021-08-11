package com.example.teampandanback.dto.comment.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CommentCreateRequestDto {
    private String content;

    @Builder
    public CommentCreateRequestDto(String content) {
        this.content = content;
    }
}
