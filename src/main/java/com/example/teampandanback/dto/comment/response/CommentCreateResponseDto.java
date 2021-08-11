package com.example.teampandanback.dto.comment.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
public class CommentCreateResponseDto {
    private Long commentId;
    private String content;
    private String writer;

    @Builder
    public CommentCreateResponseDto(Long commentId, String content, String writer) {
        this.commentId = commentId;
        this.content = content;
        this.writer = writer;
    }
}
