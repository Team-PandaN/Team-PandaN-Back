package com.example.teampandanback.dto.project.response;

import com.example.teampandanback.domain.user_project_mapping.UserProjectMapping;
import com.example.teampandanback.domain.user_project_mapping.UserProjectRole;
import com.example.teampandanback.dto.user.CrewDetailForProjectListDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ProjectEachResponseDto {
    // 프로젝트 목록 조회시 사용되는 DTO

    // 화면상에서 대표로 보여줄 크루의 프로필 수
    private static final int mainCrewSize = 3;

    // 프로젝트 id
    private Long projectId;
    // 프로젝트 명
    private String title;
    // 프로젝트 설명
    private String detail;

    // 프로젝트 안에 노트 수
    private Long noteCount;
    // 프로젝트 안에 노트 중 가장 최근 수정 일자
    private LocalDateTime recentNoteUpdateDate;

    // 프로젝트 안에 북마크 수
    private Long bookmarkCount;

    // 프로젝트에 참여하고 있는 크루 수
    private Integer crewCount;
    // 프로젝트에 참여하고 있는 크루의 프로필 목록(최대 mainCrewSize개)
    private List<String> crewProfiles;

    // 프로젝트 수정 삭제 권한 여부
    private Boolean isUpdatableAndDeletable;



    @Builder
    public ProjectEachResponseDto(Long projectId, String title, String detail, Long noteCount, Long bookmarkCount,
                                  List<String> crewProfiles, Integer crewCount, LocalDateTime recentNoteUpdateDate, Boolean isUpdatableAndDeletable) {
        this.projectId = projectId;
        this.title = title;
        this.detail = detail;
        this.noteCount = noteCount;
        this.bookmarkCount = bookmarkCount;
        this.crewProfiles = crewProfiles;
        this.crewCount = crewCount;
        this.recentNoteUpdateDate = recentNoteUpdateDate;
        this.isUpdatableAndDeletable = isUpdatableAndDeletable;
    }



    public static ProjectEachResponseDto of(ProjectDetailForProjectListDto project, List<UserProjectMapping> userProjectList,
                                            List<CrewDetailForProjectListDto> crew, Long bookmarkCount){

        // 유저가 참여하고 있는 프로젝트들의 크루 Top3 추출
        List<CrewDetailForProjectListDto> mainCrews = new ArrayList<>(crew.subList(0, Math.min(crew.size(), mainCrewSize)));

        // 유저와 해당 프로젝트의 관계 추출
        UserProjectMapping userProjectMapping = userProjectList.stream()
                .filter(userProject-> userProject.getProject().getProjectId().equals(project.getProjectId()))
                .findAny()
                .orElse(UserProjectMapping.builder()
                        .userProjectRole(UserProjectRole.CREW)
                        .build());

        return ProjectEachResponseDto.builder()
                .projectId(project.getProjectId())
                .title(project.getTitle())
                .detail(project.getDetail())
                .noteCount(project.getNoteCount())
                .recentNoteUpdateDate(project.getRecentNoteUpdateDate())
                .crewCount(crew.size())
                // 추출한 크루 Top3의 프로필 목록
                .crewProfiles(mainCrews.stream().map(mainCrew-> mainCrew.getCrewProfile()).collect(Collectors.toList()))
                .bookmarkCount(bookmarkCount==null?0:bookmarkCount)
                // OWNER 라면 수정 삭제 권한이 있다
                .isUpdatableAndDeletable(userProjectMapping.getRole()==UserProjectRole.OWNER)
                .build();
    }
}
