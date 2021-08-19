package com.example.teampandanback.domain.note;

import com.example.teampandanback.domain.project.Project;
import com.example.teampandanback.dto.note.response.NoteEachMineInTotalResponseDto;
import com.example.teampandanback.dto.note.response.noteEachSearchInTotalResponseDto;
import com.example.teampandanback.dto.note.response.NoteResponseDto;
import com.example.teampandanback.dto.note.response.NoteEachSearchInMineResponseDto;
import com.example.teampandanback.exception.ApiRequestException;
import com.example.teampandanback.utils.PandanUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.example.teampandanback.domain.note.QNote.note;
import static com.example.teampandanback.domain.project.QProject.project;
import static com.example.teampandanback.domain.user.QUser.user;
import static com.example.teampandanback.domain.user_project_mapping.QUserProjectMapping.userProjectMapping;

public class NoteRepositoryImpl implements NoteRepositoryQuerydsl{

    private final JPAQueryFactory queryFactory;
    private final PandanUtils pandanUtils;

    public NoteRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
        this.pandanUtils = new PandanUtils();
    }

    @Override
    public List<Note> findByProjectAndUser(Long projectId, Long userId) {

        return queryFactory
                .select(note)
                .from(note)
                .where(note.project.projectId.eq(projectId).and(note.user.userId.eq(userId)))
                .fetch();
    }

    @Override
    public Optional<NoteResponseDto> findByNoteId(Long noteId){
        return Optional.ofNullable(
                queryFactory
                        .select(
                                Projections.constructor
                                        (NoteResponseDto.class,
                                                note.noteId,
                                                note.title,
                                                note.content,
                                                note.deadline,
                                                note.step,
                                                project.projectId,
                                                project.title,
                                                user.name,
                                                note.createdAt,
                                                note.modifiedAt
                                                ))
                        .from(note)
                        .join(note.project, project)
                        .on(note.noteId.eq(noteId))
                        .join(note.user, user)
                        .fetchOne());
    }

    // 전체 프로젝트 중 해당 유저가 작성한 노트 조회
    @Override
    public List<NoteEachMineInTotalResponseDto> findUserNoteInTotalProject(Long userId, Pageable pageable) {
        List<NoteEachMineInTotalResponseDto> results =
                queryFactory
                        .select(
                                Projections.constructor(NoteEachMineInTotalResponseDto.class,
                                        note.noteId, note.title, note.createdAt, note.step, project.projectId, project.title
                                ))
                        .from(note)
                        .join(note.project, project)
                        .where(note.user.userId.eq(userId))
                        .orderBy(note.createdAt.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize() + 1)
                        .fetch();
        return results;
    }

    @Override
    public void deleteByProjectId(Long projectId) {
        queryFactory
                .delete(note)
                .where(note.project.projectId.eq(projectId))
                .execute();
    }

    @Override
    public List<Note> findAllNoteByProjectAndUserOrderByCreatedAtDesc(Long projectId, Long userId, Pageable pageable) {
        List<Note> results =
                queryFactory
                        .select(note)
                        .from(note)
                        .where(note.project.projectId.eq(projectId).and(note.user.userId.eq(userId)))
                        .orderBy(note.createdAt.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize() + 1)
                        .fetch();
        return results;
    }

    // keyword로 내가 참여하고 있는 프로젝트 안에서 노트 검색, 제목으로만 검색합니다.
    @Override
    public List<noteEachSearchInTotalResponseDto> findNotesByUserIdAndKeywordInTotal(Long userId, List<String> keywordList) {
        BooleanBuilder builder = pandanUtils.searchByTitleBooleanBuilder(keywordList);

        List<Long> projectIdList = queryFactory
                .select(userProjectMapping.project.projectId)
                .from(userProjectMapping)
                .where(userProjectMapping.user.userId.eq(userId))
                .fetch();

        return queryFactory
                .select(Projections.constructor(noteEachSearchInTotalResponseDto.class,
                        note.noteId, note.title, note.step, project.projectId, project.title, user.name, note.createdAt))
                .from(note)
                .join(note.project, project)
                .join(note.user, user)
                .where(note.project.projectId.in(projectIdList).and(builder))
                .orderBy(note.modifiedAt.desc())
                .fetch();
    }

    // keyword로 내가 쓴 문서 안에서 노트 검색, 제목으로만 검색합니다.
    @Override
    public List<NoteEachSearchInMineResponseDto> findNotesByUserIdAndKeywordInMine(Long userId, List<String> keywordList) {
        BooleanBuilder builder = pandanUtils.searchByTitleBooleanBuilder(keywordList);

        return queryFactory
                .select(Projections.constructor(NoteEachSearchInMineResponseDto.class,
                        note.noteId, note.title, note.step, project.projectId, project.title, note.createdAt))
                .from(note)
                .where(note.user.userId.eq(userId).and(builder))
                .orderBy(note.modifiedAt.desc())
                .join(note.project, project)
                .fetch();
    }

    @Override
    public List<Note> findAllByProjectOrderByCreatedAtDesc(Project project, Pageable pageable) {
        List<Note> results =
                queryFactory
                        .select(note)
                        .from(note)
                        .where(note.project.eq(project))
                        .orderBy(note.createdAt.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize() + 1)
                        .fetch();
        return results;
    }

    @Override
    public Long countByProjectId(Long projectId) {
        return queryFactory
                .selectFrom(note)
                .where(note.project.projectId.eq(projectId))
                .fetchCount();
    }

    @Override
    public List<Note> findAllByProjectId(Long projectId) {
        return queryFactory
                .selectFrom(note)
                .where(note.project.projectId.eq(projectId))
                .fetch();
    }

    @Override
    public List<Note> findNotesByNoteIdList(List<Long> noteIdList) {
        return queryFactory
                .selectFrom(note)
                .where(note.noteId.in(noteIdList))
                .orderBy(note.step.desc())
                .fetch();
    }
}
