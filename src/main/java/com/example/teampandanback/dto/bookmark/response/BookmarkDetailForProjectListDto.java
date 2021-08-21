package com.example.teampandanback.dto.bookmark.response;

import lombok.Getter;

@Getter
public class BookmarkDetailForProjectListDto {
    // 프로젝트 목록 조회시 사용되는 북마크 정보

    // 프로젝트 ID
    private Long projectId;
    // 프로젝트에 있는 북마크 총 갯수
    private Long bookmarkCount;

    public BookmarkDetailForProjectListDto(Long projectId, Long bookmarkCount) {
        this.projectId = projectId;
        this.bookmarkCount = bookmarkCount;
    }
}
