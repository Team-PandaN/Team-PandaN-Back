package com.example.teampandanback.domain.bookmark;

import com.example.teampandanback.dto.note.response.NoteEachBookmarkedResponseDto;
import com.example.teampandanback.dto.note.response.NoteEachSearchInBookmarkResponseDto;
import com.example.teampandanback.utils.PandanUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

import static com.example.teampandanback.domain.bookmark.QBookmark.bookmark;
import static com.example.teampandanback.domain.note.QNote.note;
import static com.example.teampandanback.domain.project.QProject.project;
import static com.example.teampandanback.domain.user.QUser.user;

public class BookmarkRepositoryImpl implements BookmarkRepositoryQuerydsl {
    private final JPAQueryFactory queryFactory;
    private PandanUtils pandanUtils;

    public BookmarkRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
        this.pandanUtils = new PandanUtils();
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
    public List<NoteEachBookmarkedResponseDto> findNoteByUserIdInBookmark(Long userId) {
        List<Long> noteIdList = queryFactory
                .select(bookmark.note.noteId)
                .from(bookmark)
                .where(bookmark.user.userId.eq(userId))
                .fetch();

        return queryFactory
                .select(Projections.constructor(NoteEachBookmarkedResponseDto.class,
                        note.noteId, note.title, note.step, project.projectId, project.title, user.name))
                .from(note)
                .where(note.noteId.in(noteIdList))
                .join(note.project, project)
                .join(note.user, user)
                .fetch();
    }

    // Note 에 연관된 북마크 삭제
    @Override
    public void deleteByNote(Long noteId) {
        queryFactory
                .delete(bookmark)
                .where(bookmark.note.noteId.eq(noteId))
                .execute();
    }

    // keyword로 북마크에서 검색, 제목만 검색합니다.
    @Override
    public List<NoteEachSearchInBookmarkResponseDto> findNotesByUserIdAndKeywordInBookmarks(Long userId, List<String> keywordList) {
        BooleanBuilder builder = pandanUtils.searchByTitleBooleanBuilder(keywordList);

        List<Long> noteIdList = queryFactory
                .select(bookmark.note.noteId)
                .from(bookmark)
                .where(bookmark.user.userId.eq(userId))
                .fetch();

        return queryFactory
                .select(Projections.constructor(NoteEachSearchInBookmarkResponseDto.class,
                        note.noteId, note.title, note.step, project.projectId, project.title, user.name))
                .from(note)
                .where(note.noteId.in(noteIdList).and(builder))
                .orderBy(note.modifiedAt.desc())
                .join(note.project, project)
                .join(note.user, user)
                .fetch();
    }
}
