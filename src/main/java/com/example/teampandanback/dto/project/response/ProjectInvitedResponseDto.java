package com.example.teampandanback.dto.project.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProjectInvitedResponseDto {
    private Long projectId;

    @Builder
    public ProjectInvitedResponseDto(Long projectId) {
        this.projectId = projectId;
    }
}
