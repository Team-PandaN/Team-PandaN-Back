package com.example.teampandanback.dto.project.response;

import com.example.teampandanback.domain.user_project_mapping.UserProjectRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProjectDetailResponseDto {

    // 프로젝트의 id (PK 값)
    private long projectId;
    // 프로젝트 명
    private String title;
    // 프로젝트 설명
    private String detail;

    // 해당 유저의 프로젝트 수정, 삭제 가능 여부
    public Boolean isUpdatableAndDeletable;

    // 프로젝트의 참여멤버 수
    private long crewCount;

    @Builder
    public ProjectDetailResponseDto(long projectId, String title, String detail, UserProjectRole role) {
        this.projectId = projectId;
        this.title = title;
        this.detail = detail;
        // Owner 권한이라면 프로젝트의 수정 삭제가 가능하다.
        this.isUpdatableAndDeletable = role.equals(UserProjectRole.OWNER);
        }

    public void updateCrewCount(long crewCount){
        this.crewCount = crewCount;
    }

}
