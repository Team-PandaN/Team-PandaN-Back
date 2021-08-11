package com.example.teampandanback.dto.comment.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class CommentReadListResponseDto {
    private List<CommentReadEachResponseDto> commentList;

    @Builder
    public CommentReadListResponseDto(List<CommentReadEachResponseDto> commentList) {
        this.commentList = commentList;
    }
}

