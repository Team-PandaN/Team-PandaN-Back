package com.example.teampandanback.domain.bookmark;

import com.example.teampandanback.domain.note.QNote;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

import java.awt.print.Book;
import java.util.Optional;

import static com.example.teampandanback.domain.bookmark.QBookmark.bookmark;
import static com.example.teampandanback.domain.note.QNote.note;

public class BookmarkRepositoryImpl implements BookmarkRepositoryQuerydsl {
    private final JPAQueryFactory queryFactory;

    public BookmarkRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public void deleteByProjectId(long projectId) {
        queryFactory
                .delete(bookmark)
                .where(
                        bookmark.note.noteId.in(
                                JPAExpressions
                                        .select(note.noteId)
                                        .from(note)
                                        .where(note.project.projectId.eq(projectId))
                        )
                )
                .execute();
    }

    @Override
    public Optional<Bookmark> findByUserIdAndNoteId(Long userId, Long noteId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(bookmark)
                .where(bookmark.note.noteId.eq(noteId).and(bookmark.user.userId.eq(userId)))
                .fetchOne());
    }
}
