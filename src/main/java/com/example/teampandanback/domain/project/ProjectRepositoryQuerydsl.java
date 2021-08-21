package com.example.teampandanback.domain.project;

import com.example.teampandanback.dto.project.response.ProjectDetailForProjectListDto;

import java.util.List;

public interface ProjectRepositoryQuerydsl {

    // 유저가 가진 프로젝트의 총 노트 수와 가장 최근에 수정한 노트의 수정날짜와 프로젝트 정보 조회
    List<ProjectDetailForProjectListDto> findProjectDetailForProjectList(List<Long> projectIdList);
}
