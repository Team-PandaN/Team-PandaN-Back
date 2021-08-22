package com.example.teampandanback.domain.user_project_mapping;

import com.example.teampandanback.dto.project.request.ProjectResponseDto;
import com.example.teampandanback.dto.project.response.ProjectDetailResponseDto;
import com.example.teampandanback.dto.project.response.ProjectSidebarResponseDto;
import com.example.teampandanback.dto.user.CrewDetailForProjectListDto;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

public interface UserProjectMappingRepositoryQuerydsl {

    // 프로젝트 상세 조회
    Optional<ProjectDetailResponseDto> findProjectDetail(long userId, long projectId);

    // 프로젝트의 참여 멤버 수
    long findCountProjectMember(long projectId);

    List<ProjectResponseDto> findProjectByUser_UserId(Long userId);

    // 사이드 바에 들어갈 프로젝트의 목록 (최대 readSize 개)
    List<ProjectSidebarResponseDto> findProjectListTopSize(long userId, int readSize);

    //x 유저가 y 프로젝트에 속해 있는지 여부를 판단, fetchOne()
    Optional<UserProjectMapping> findByUserIdAndProjectId(Long userId, Long projectId);

    //x 유저가 참여해있는 모든 유저-프로젝트를 호출
    List<UserProjectMapping> findByUserId(Long userId);

    // x 유저가 참여해 있는 모든 프로젝트들의 ID 목록을 조회
    List<Long> findProjectIdListByUserId(Long userId);

    UserProjectMapping findByUserIdAndProjectIdJoin(Long userId, Long projectId);

    @Modifying(clearAutomatically = true)
    void deleteByProjectId(Long projectId);

    // 주어진 프로젝트에 참여하고 있는 크루 정보 조회
    List<CrewDetailForProjectListDto> findCrewDetailForProjectList(List<Long> projectIdList);

    Long countByProjectId(Long projectId);
}
