package com.example.teampandanback.domain.user_project_mapping;

import com.example.teampandanback.dto.project.response.ProjectDetailResponseDto;
import com.example.teampandanback.dto.project.request.ProjectResponseDto;
import com.example.teampandanback.dto.project.response.ProjectSidebarResponseDto;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

public interface UserProjectMappingRepositoryQuerydsl {

    // 프로젝트 상세 조회
    Optional<ProjectDetailResponseDto> findProjectDetail(long userId, long projectId);

    // 프로젝트의 참여 멤버 수
    long findCountProjectMember(long projectId);

    List<ProjectResponseDto> findProjectByUser_UserId(Long userId);
    List<ProjectSidebarResponseDto> findProjectListTopSize(long userId, Long readSize);

    //x 유저가 y 프로젝트에 속해 있는지 여부를 판단, fetchOne()
    Optional<UserProjectMapping> findByUserIdAndProjectId(Long userId, Long projectId);


    UserProjectMapping findByUserIdAndProjectIdJoin(Long userId, Long projectId);

    @Modifying(clearAutomatically = true)
    void deleteByProjectId(Long projectId);

}
