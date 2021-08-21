package com.example.teampandanback.service;

import com.example.teampandanback.domain.file.File;
import com.example.teampandanback.domain.file.FileRepository;
import com.example.teampandanback.domain.user.User;
import com.example.teampandanback.domain.user_project_mapping.UserProjectMapping;
import com.example.teampandanback.domain.user_project_mapping.UserProjectMappingRepository;
import com.example.teampandanback.dto.file.response.FileDeleteResponseDto;
import com.example.teampandanback.exception.ApiRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FileService {

    private final FileRepository fileRepository;
    private final UserProjectMappingRepository userProjectMappingRepository;

    @Transactional
    public FileDeleteResponseDto deleteFile(Long fileId, User currentUser) {

        File file = fileRepository.findById(fileId).orElseThrow(
                () -> new ApiRequestException("이미 삭제된 파일입니다.")
        );
        Long projectId = file.getNote().getProject().getProjectId();

        List<UserProjectMapping> userProjectMappingList =
                userProjectMappingRepository.findByUserId(currentUser.getUserId());

        List<Long> projectIdList = new ArrayList<>();

        for (int i = 0; i < userProjectMappingList.size(); i++) {
            projectIdList.add(userProjectMappingList.get(i).getProject().getProjectId());
        }
        if (!projectIdList.contains(projectId)) {
            throw new ApiRequestException("파일을 삭제할 권한이 없습니다.");
        }

        fileRepository.delete(file);

        return FileDeleteResponseDto.builder()
                .fileId(fileId)
                .build();

    }
}
