package com.example.teampandanback.domain.project;

import com.example.teampandanback.domain.Timestamped;
import com.example.teampandanback.dto.project.ProejctRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Project extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROJECT_ID")
    private Long projectId;

    @Column
    private String title;

    @Column(columnDefinition = "TEXT")
    private String detail;

    @Builder
    public Project(String title, String detail){
        this.title = title;
        this.detail = detail;
    }

    public static Project of(ProejctRequestDto requestDto){
        return Project.builder()
                .title(requestDto.getTitle())
                .detail(requestDto.getDetail())
                .build();
    }

    // Project 수정
    public void update(ProejctRequestDto requestDto){
        this.title = requestDto.getTitle();
        this.detail = requestDto.getDetail();
    }
}
