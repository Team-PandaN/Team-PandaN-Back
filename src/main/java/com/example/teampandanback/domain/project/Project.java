package com.example.teampandanback.domain.project;

import com.example.teampandanback.domain.Timestamped;
import com.example.teampandanback.dto.project.request.ProjectRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@DynamicUpdate
@Entity
public class Project extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROJECT_ID")
    private Long projectId;

    @Column(name="TITLE")
    private String title;

    @Column(columnDefinition = "TEXT", name = "DETAIL")
    private String detail;

    @Builder
    public Project(String title, String detail){
        this.title = title;
        this.detail = detail;
    }

    public static Project toEntity(ProjectRequestDto requestDto){
        return Project.builder()
                .title(requestDto.getTitle())
                .detail(requestDto.getDetail())
                .build();
    }

    // Project 수정
    public void update(ProjectRequestDto requestDto){
        this.title = requestDto.getTitle();
        this.detail = requestDto.getDetail();
    }
}
