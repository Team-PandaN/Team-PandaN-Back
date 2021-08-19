package com.example.teampandanback.dto.project.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CrewResponseDto {
    private Long userId;
    private String userName;
    private String userPicture;

    @Builder
    public CrewResponseDto(Long userId, String userName, String userPicture) {
        this.userId = userId;
        this.userName = userName;
        this.userPicture = userPicture;
    }
}
