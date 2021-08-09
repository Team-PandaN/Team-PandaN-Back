package com.example.teampandanback.dto.user;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserDetailResponseDto {
    private String name;
    private String email;
    private String picture;

    @Builder
    public UserDetailResponseDto(String name, String email, String picture) {
        this.name = name;
        this.email = email;
        this.picture = picture;
    }
}
