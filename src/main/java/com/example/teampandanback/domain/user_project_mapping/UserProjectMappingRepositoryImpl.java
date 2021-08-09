package com.example.teampandanback.domain.user_project_mapping;

import com.example.teampandanback.dto.project.ProjectDetailResponseDto;
import com.example.teampandanback.dto.project.ProjectResponseDto;
import com.example.teampandanback.dto.project.ProjectSidebarResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.example.teampandanback.domain.project.QProject.project;
import static com.example.teampandanback.domain.user_project_mapping.QUserProjectMapping.userProjectMapping;

public class UserProjectMappingRepositoryImpl implements UserProjectMappingRepositoryQuerydsl {

    private final JPAQueryFactory queryFactory;

    public UserProjectMappingRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    // 프로젝트의 상세 조회
    @Override
    public Optional<ProjectDetailResponseDto> findProjectDetail(long userId, long projectId) {
        return Optional.ofNullable(
                queryFactory
                        .select(
                                Projections.constructor(ProjectDetailResponseDto.class,
                                        project.projectId, project.title, project.detail, userProjectMapping.role))
                        .from(userProjectMapping)
                        .join(userProjectMapping.project, project)
                        .on(userProjectMapping.project.projectId.eq(projectId).and(userProjectMapping.user.userId.eq(userId)))
                        .fetchOne()
        );
    }

    // 프로젝트 참여 멤버 수 조회
    @Override
    public long findCountProjectMember(long projectId) {
        return queryFactory
                .select(userProjectMapping)
                .from(userProjectMapping)
                .where(userProjectMapping.project.projectId.eq(projectId))
                .fetchCount();
    }

    // 사이드 바에 들어갈 프로젝트의 목록 (최대 5개)
    @Override
    public List<ProjectSidebarResponseDto> findProjectListTop5(long userId) {
        return queryFactory
                .select(
                        Projections.constructor(ProjectSidebarResponseDto.class,
                                project.projectId, project.title
                        ))
                .from(userProjectMapping)
                .join(userProjectMapping.project, project)
                .where(userProjectMapping.user.userId.eq(userId))
                .limit(5)
                .fetch();
    }


    @Override
    public List<ProjectResponseDto> findProjectByUser_UserId(Long userId) {
        return queryFactory
                .select(
                        Projections.constructor(ProjectResponseDto.class,
                        project.projectId, project.title, project.detail
                ))
                .from(userProjectMapping)
                .join(userProjectMapping.project, project)
                .where(userProjectMapping.user.userId.eq(userId))
                .fetch();
    }
}
