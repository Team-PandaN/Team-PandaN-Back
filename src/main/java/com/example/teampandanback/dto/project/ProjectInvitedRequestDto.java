package com.example.teampandanback.dto.project;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProjectInvitedRequestDto {
    private final String inviteCode;

    @Builder
    public ProjectInvitedRequestDto(String inviteCode) {
        this.inviteCode = inviteCode;
    }
}
