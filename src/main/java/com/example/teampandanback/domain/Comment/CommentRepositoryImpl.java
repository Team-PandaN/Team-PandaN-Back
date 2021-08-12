package com.example.teampandanback.domain.Comment;


import com.example.teampandanback.exception.ApiRequestException;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;
import static com.example.teampandanback.domain.Comment.QComment.comment;
import static com.example.teampandanback.domain.note.QNote.note;

public class CommentRepositoryImpl implements CommentRepositoryQuerydsl {

    private final JPAQueryFactory queryFactory;

    public CommentRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);


    }

    @Override
    public List<Comment> findByNoteId(Long noteId) {
        return queryFactory
                .selectFrom(comment)
                .where(comment.note.noteId.eq(noteId))
                .fetch();
    }

    @Override
    public void deleteByCommentIdAndUserId(Long commentId, Long userId) {
        Long distinctNum = queryFactory
                .delete(comment)
                .where(comment.commentId.eq(commentId), comment.user.userId.eq(userId))
                .execute();

        // execute를 실행하면 삭제를 수행하고 삭제된 엔티티의 개수를 리턴한다.
        // CommentId가 PK값이므로 정상적으로 삭제되었을때 distincNum 이 1이다.
        if (!distinctNum.equals(1L)) {
            throw new ApiRequestException("댓글을 삭제할수 없습니다.");
        }
    }

    @Override
    public void deleteCommentByNoteId(Long noteId) {
        queryFactory
                .delete(comment)
                .where(comment.note.noteId.eq(noteId))
                .execute();
    }

    @Override
    public void deleteCommentByProjectId(Long projectId) {
        queryFactory
                .delete(comment)
                .where(
                        comment.note.noteId.in(
                                JPAExpressions
                                        .select(note.noteId)
                                        .from(note)
                                        .where(note.project.projectId.eq(projectId))
                        )
                )
                .execute();
    }

}

