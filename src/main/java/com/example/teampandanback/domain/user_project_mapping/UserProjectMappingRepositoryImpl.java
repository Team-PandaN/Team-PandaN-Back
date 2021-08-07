package com.example.teampandanback.domain.user_project_mapping;

import com.example.teampandanback.dto.project.ProjectDetailResponseDto;
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
                        .on(userProjectMapping.project.projectId.eq(projectId))
                        .where(userProjectMapping.user.userId.eq(userId))
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

}
