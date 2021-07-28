package com.example.teampandanback.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    GUEST("ROLE_GUEST", "손님"),
    USER("ROLE_USER","일반 사용자"),
    OWNER("ROLE_OWNER","프로젝트 소유주");

    private final String key;
    private final String title;
}
