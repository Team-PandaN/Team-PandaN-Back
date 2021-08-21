package com.example.teampandanback.domain.project;

import com.example.teampandanback.dto.project.response.ProjectDetailForProjectListDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

import java.util.List;

import static com.example.teampandanback.domain.note.QNote.note;
import static com.example.teampandanback.domain.project.QProject.project;

public class ProjectRepositoryImpl implements ProjectRepositoryQuerydsl{
    private final JPAQueryFactory queryFactory;

    public ProjectRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    // 유저가 가진 프로젝트의 총 노트 수와 가장 최근에 수정한 노트의 수정날짜와 프로젝트 정보 조회
    @Override
    public List<ProjectDetailForProjectListDto> findProjectDetailForProjectList(List<Long> projectIdList) {
        return queryFactory
                .select(
                        Projections.constructor(
                                ProjectDetailForProjectListDto.class,
                                project.projectId, project.title, project.detail, note.count(), note.modifiedAt.max()))
                .from(project)
                .leftJoin(note)
                .on(note.project.eq(project))
                .where(project.projectId.in(projectIdList))
                .groupBy(project.projectId)
                .orderBy(note.modifiedAt.max().desc())
                .fetch();
    }
}
