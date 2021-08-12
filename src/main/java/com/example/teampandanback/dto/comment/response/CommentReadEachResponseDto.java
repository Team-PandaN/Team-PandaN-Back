package com.example.teampandanback.dto.comment.response;

import com.example.teampandanback.domain.Comment.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
public class CommentReadEachResponseDto {
    private Long commentId;
    private String content;
    private String writer;


    @Builder
    public CommentReadEachResponseDto(Long commentId, String content, String writer) {
        this.commentId = commentId;
        this.content = content;
        this.writer = writer;
    }

    public static CommentReadEachResponseDto fromEntity(Comment comment){
        return CommentReadEachResponseDto.builder()
                .commentId(comment.getCommentId())
                .content(comment.getContent())
                .writer(comment.getUser().getName())
                .build();

    }
}
