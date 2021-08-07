package com.example.teampandanback.domain.user_project_mapping;

import com.example.teampandanback.dto.project.ProjectDetailResponseDto;
import com.example.teampandanback.dto.project.ProjectSidebarResponseDto;

import java.util.List;
import java.util.Optional;

public interface UserProjectMappingRepositoryQuerydsl {

    // 프로젝트 상세 조회
    Optional<ProjectDetailResponseDto> findProjectDetail(long userId, long projectId);

    // 프로젝트의 참여 멤버 수
    long findCountProjectMember(long projectId);

    // 사이드 바에 들어갈 프로젝트의 목록 (최대 5개)
    List<ProjectSidebarResponseDto> findProjectListTop5(long userId);
}
