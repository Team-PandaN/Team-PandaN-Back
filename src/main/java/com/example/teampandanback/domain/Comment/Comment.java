package com.example.teampandanback.domain.Comment;

import com.example.teampandanback.domain.note.Note;
import com.example.teampandanback.domain.user.User;
import com.example.teampandanback.dto.comment.request.CommentUpdateRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@DynamicUpdate
@Entity
public class Comment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMENT_ID")
    private Long commentId;

    @Column(name = "CONTENT", columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "USER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="NOTE_ID")
    private Note note;

    @Builder
    public Comment(String content, User user, Note note) {
        this.content = content;
        this.user = user;
        this.note = note;
    }

    // 댓글 수정
    public Comment update(CommentUpdateRequestDto commentUpdateRequestDto){
        this.content = commentUpdateRequestDto.getContent();
        return this;
    }
}
