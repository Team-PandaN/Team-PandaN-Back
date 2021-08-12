package com.example.teampandanback.dto.comment.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentDeleteResponseDto {
    private Long commentId;

    @Builder
    public CommentDeleteResponseDto(Long commentId) {
        this.commentId = commentId;
    }
}
