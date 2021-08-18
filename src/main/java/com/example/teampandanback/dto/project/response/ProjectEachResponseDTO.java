package com.example.teampandanback.dto.project.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//3.0 프로젝트 목록 조회
//{
//        "projectId": 42,
//        "title": "프로젝트2",
//        "noteCount": 20,
//        "bookmarkcount": 10,
//        "crewProfile": ["이미지1.jpg","이미지2.jpg"],
//        "crewCount": 5,
//        "recentNoteUpdateDate": "2021-08-16"
//        "detail" : "새 프로젝트 설명"
//        },
@Getter
@Setter
public class ProjectEachResponseDTO {
    private Long projectId;
    private String title;
    private String detail;
    private Long noteCount;
    private Long bookmarkCount;
    private List<String> crewProfiles  = new ArrayList<>();
    private Long crewCount;
    private LocalDateTime recentNoteUpdateDate;

    @Builder
    public ProjectEachResponseDTO(Long projectId, String title, String detail, Long noteCount, Long bookmarkCount, List<String> crewProfiles, Long crewCount, LocalDateTime recentNoteUpdateDate) {
        this.projectId = projectId;
        this.title = title;
        this.detail = detail;
        this.noteCount = noteCount;
        this.bookmarkCount = bookmarkCount;
        this.crewProfiles = crewProfiles;
        this.crewCount = crewCount;
        this.recentNoteUpdateDate = recentNoteUpdateDate;
    }
}
