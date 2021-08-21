package com.example.teampandanback.dto.file.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class FileCreateRequestDto {
    private List<FileDetailRequestDto> files;

}
