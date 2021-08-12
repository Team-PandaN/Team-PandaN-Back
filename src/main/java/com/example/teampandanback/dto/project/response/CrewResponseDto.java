package com.example.teampandanback.dto.project.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CrewResponseDto {
    private Long userId;
    private String userName;

    @Builder
    public CrewResponseDto(Long userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }
}
