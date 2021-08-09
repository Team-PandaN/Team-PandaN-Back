package com.example.teampandanback.domain.bookmark;

import com.example.teampandanback.domain.note.QNote;
import com.example.teampandanback.dto.note.response.NoteEachBookmarkedResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.teampandanback.domain.bookmark.QBookmark.bookmark;
import static com.example.teampandanback.domain.note.QNote.note;
import static com.example.teampandanback.domain.project.QProject.project;
import static com.example.teampandanback.domain.user.QUser.user;

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

    @Override
    public List<NoteEachBookmarkedResponseDto> findByUserId(Long userId){
        List<Long> ids = queryFactory
                .select(bookmark.note.noteId)
                .from(bookmark)
                .where(bookmark.user.userId.eq(userId))
                .fetch();

        if(ids.isEmpty()){
            return new ArrayList<>();
        }

        return queryFactory
                .select(Projections.constructor(NoteEachBookmarkedResponseDto.class,
                        note.noteId, note.title, note.step, project.projectId, project.title, user.name))
                .from(note)
                .where(note.noteId.in(ids))
                .join(note.project, project)
                .join(note.user, user)
                .fetch();

    }
}
