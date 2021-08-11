package com.example.teampandanback.domain.Comment;


import com.example.teampandanback.exception.ApiRequestException;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

import static com.example.teampandanback.domain.Comment.QComment.comment;
import static com.example.teampandanback.domain.note.QNote.note;

public class CommentRepositoryImpl implements CommentRepositoryQuerydsl {

    private final JPAQueryFactory queryFactory;

    public CommentRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);


    }
    // 코멘트 삭제 v1
    @Override
    public void deleteByCommentIdAndUserId(Long commentId, Long userId) {
         Long distinctNum = queryFactory
                .delete(comment)
                .where(comment.commentId.eq(commentId), comment.user.userId.eq(userId))
                .execute();

         // 삭제된 파일이 존재하면 distincNum 이 1이다.
        if (!distinctNum.equals(1L)) {
            throw new ApiRequestException("댓글을 삭제할수 없습니다.");
        }

    }

    // 코멘트 삭제 V2
    @Override
    public Comment findByCommentIdAndUserId(Long commentId, Long userId) {
        return queryFactory
                .select(comment)
                .from(comment)
                .where(comment.commentId.eq(commentId), comment.user.userId.eq(userId))
                .fetchOne();
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

