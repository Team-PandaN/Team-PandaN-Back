package com.example.teampandanback.dto.comment.response;

import com.example.teampandanback.domain.Comment.Comment;
import com.example.teampandanback.dto.comment.request.CommentUpdateRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class CommentUpdateResponseDto {
    private Long commentId;
    private String content;
    private String writer;


    @Builder
    public CommentUpdateResponseDto(Long commentId, String content, String writer) {
        this.commentId = commentId;
        this.content = content;
        this.writer = writer;
    }

    public static CommentUpdateResponseDto fromEntity(Comment comment){
        return CommentUpdateResponseDto.builder()
                .commentId(comment.getCommentId())
                .content(comment.getContent())
                .writer(comment.getUser().getName())
                .build();
    }
}
