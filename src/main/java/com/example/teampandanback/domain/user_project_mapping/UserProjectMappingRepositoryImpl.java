package com.example.teampandanback.domain.user_project_mapping;

import com.example.teampandanback.dto.project.response.ProjectDetailResponseDto;
import com.example.teampandanback.dto.project.request.ProjectResponseDto;
import com.example.teampandanback.dto.project.response.ProjectSidebarResponseDto;
import com.example.teampandanback.exception.ApiRequestException;
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

    // 사이드 바에 들어갈 프로젝트의 목록 (최대 N개)
    @Override
    public List<ProjectSidebarResponseDto> findProjectListTopSize(long userId, Long readSize) {
        return queryFactory
                .select(
                        Projections.constructor(ProjectSidebarResponseDto.class,
                                project.projectId, project.title
                        ))
                .from(userProjectMapping)
                .join(userProjectMapping.project, project)
                .where(userProjectMapping.user.userId.eq(userId))
                .limit(readSize)
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

    @Override
    public Optional<UserProjectMapping> findByUserIdAndProjectId(Long userId, Long projectId) {
        return Optional.ofNullable(queryFactory
                .select(userProjectMapping)
                .from(userProjectMapping)
                .where(userProjectMapping.user.userId.eq(userId), userProjectMapping.project.projectId.eq(projectId))
                .fetchFirst());
    }

    @Override
    public void deleteByProjectId(Long projectId) {
        queryFactory
                .delete(userProjectMapping)
                .where(userProjectMapping.project.projectId.eq(projectId))
                .execute();
    }



    @Override
    public UserProjectMapping findByUserIdAndProjectIdJoin(Long userId, Long projectId) {
        return Optional.ofNullable(queryFactory
                .select(userProjectMapping)
                .from(userProjectMapping)
                .join(userProjectMapping.project).fetchJoin()
                .where(userProjectMapping.user.userId.eq(userId), userProjectMapping.project.projectId.eq(projectId))
                .fetchFirst())
                .orElseThrow(() -> new ApiRequestException("프로젝트에 참가한 사용자가 아닙니다."));
    }
}
