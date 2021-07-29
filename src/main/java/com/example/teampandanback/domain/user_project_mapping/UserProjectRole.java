package com.example.teampandanback.domain.user_project_mapping;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserProjectRole {

    CREW("ROLE_CREW","프로젝트 참여자"),
    OWNER("ROLE_OWNER","프로젝트 소유주");

    private final String key;
    private final String title;
}
