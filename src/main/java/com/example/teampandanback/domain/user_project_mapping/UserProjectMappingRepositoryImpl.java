package com.example.teampandanback.domain.user_project_mapping;

import com.example.teampandanback.dto.project.request.ProjectResponseDto;
import com.example.teampandanback.dto.project.response.ProjectDetailResponseDto;
import com.example.teampandanback.dto.project.response.ProjectSidebarResponseDto;
import com.example.teampandanback.dto.user.CrewDetailForProjectListDto;
import com.example.teampandanback.exception.ApiRequestException;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.example.teampandanback.domain.note.QNote.note;
import static com.example.teampandanback.domain.project.QProject.project;
import static com.example.teampandanback.domain.user.QUser.user;
import static com.example.teampandanback.domain.user_project_mapping.QUserProjectMapping.userProjectMapping;

public class UserProjectMappingRepositoryImpl implements UserProjectMappingRepositoryQuerydsl {

    private final JPAQueryFactory queryFactory;

    public UserProjectMappingRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public List<UserProjectMapping> findByUserId(Long userId) {
        return queryFactory
                .select(userProjectMapping)
                .from(userProjectMapping)
                .where(userProjectMapping.user.userId.eq(userId))
                .fetch();
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
    public List<ProjectSidebarResponseDto> findProjectListTopSize(long userId, int readSize) {
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


    // 주어진 프로젝트에 참여하고 있는 크루 정보 조회
    @Override
    public List<CrewDetailForProjectListDto> findCrewDetailForProjectList(List<Long> projectIdList) {

        return queryFactory
                .select(
                        Projections.constructor(CrewDetailForProjectListDto.class,
                                userProjectMapping.project.projectId, user.userId, user.picture)
                )
                .from(userProjectMapping)
                .join(userProjectMapping.user, user)
                .where(userProjectMapping.project.projectId.in(projectIdList))
                .fetch();

    }

    @Override
    public Long countByProjectId(Long projectId) {
        return queryFactory
                .selectFrom(userProjectMapping)
                .where(userProjectMapping.project.projectId.eq(projectId))
                .fetchCount();
    }

    // 유저가 참여해 있는 모든 프로젝트들의 ID 목록을 조회
    @Override
    public List<Long> findProjectIdListByUserId(Long userId) {

        return queryFactory
                .select(userProjectMapping.project.projectId)
                .from(userProjectMapping)
                .where(userProjectMapping.user.userId.eq(userId))
                .fetch();
    }
}
