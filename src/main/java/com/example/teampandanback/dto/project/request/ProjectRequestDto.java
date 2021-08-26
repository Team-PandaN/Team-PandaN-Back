package com.example.teampandanback.dto.project.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProjectRequestDto {
    private String title;
    private String detail;

    @Builder
    public ProjectRequestDto(String title, String detail) {
        this.title = title;
        this.detail = detail;
    }
}
