package com.example.teampandanback.dto.user;

import lombok.Getter;

@Getter
public class CrewDetailForProjectListDto {
    // 프로젝트 목록 조회시 사용되는 크루(유저) 정보

    // 프로젝트 ID
    private Long projectId;
    // 프로젝트에 참여하고 있는 크루 ID
    private Long crewId;
    // 프로젝트에 참여하고 있는 크루의 프로필
    private String crewProfile;

    public CrewDetailForProjectListDto(Long projectId, Long crewId, String crewProfile) {
        this.projectId = projectId;
        this.crewId = crewId;
        this.crewProfile = crewProfile;
    }
}
