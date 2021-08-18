package com.example.teampandanback.dto.comment.response;

import com.example.teampandanback.domain.Comment.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class CommentReadEachResponseDto {
    private Long commentId;
    private String content;
    private String writer;
    private LocalDateTime modifiedAt;
    private String writerProfileImg;


    @Builder
    public CommentReadEachResponseDto(Long commentId, String content, String writer, LocalDateTime modifiedAt, String writerProfileImg) {
        this.commentId = commentId;
        this.content = content;
        this.writer = writer;
        this.modifiedAt = modifiedAt;
        this.writerProfileImg = writerProfileImg;
    }

    public static CommentReadEachResponseDto fromEntity(Comment comment){
        return CommentReadEachResponseDto.builder()
                .commentId(comment.getCommentId())
                .content(comment.getContent())
                .writer(comment.getUser().getName())
                .modifiedAt(comment.getModifiedAt())
                .writerProfileImg(comment.getUser().getPicture())
                .build();

    }
}
