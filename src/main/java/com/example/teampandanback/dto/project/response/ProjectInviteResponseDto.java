package com.example.teampandanback.dto.project.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ProjectInviteResponseDto {
    private final String inviteCode;

    @Builder
    public ProjectInviteResponseDto(String inviteCode) {
        this.inviteCode = inviteCode;
    }
}
