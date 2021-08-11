package com.example.teampandanback.domain.Comment;

import com.example.teampandanback.domain.note.QNote;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;

import static com.example.teampandanback.domain.Comment.QComment.comment;

public class CommentRepositoryImpl implements CommentRepositoryQuerydsl{
    private final JPAQueryFactory queryFactory;

    public CommentRepositoryImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Comment> findByNoteId(Long noteId) {
        return queryFactory
                .selectFrom(comment)
                .where(comment.note.noteId.eq(noteId))
                .fetch();
    }
}
