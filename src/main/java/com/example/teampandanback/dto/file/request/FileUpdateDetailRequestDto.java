package com.example.teampandanback.dto.file.request;

import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class FileUpdateDetailRequestDto {

    private Long fileId;
    private String fileName;
    private String fileUrl;

}
