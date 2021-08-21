package com.example.teampandanback.service;

import com.example.teampandanback.domain.file.File;
import com.example.teampandanback.domain.file.FileRepository;
import com.example.teampandanback.domain.note.Note;
import com.example.teampandanback.domain.note.NoteRepository;
import com.example.teampandanback.domain.project.Project;
import com.example.teampandanback.domain.user.User;
import com.example.teampandanback.domain.user.UserRepository;
import com.example.teampandanback.domain.user_project_mapping.UserProjectMapping;
import com.example.teampandanback.domain.user_project_mapping.UserProjectMappingRepository;
import com.example.teampandanback.dto.file.request.FileCreateRequestDto;
import com.example.teampandanback.dto.file.request.FileDetailRequestDto;
import com.example.teampandanback.dto.file.response.FileCreateResponseDto;
import com.example.teampandanback.dto.file.response.FileDeleteResponseDto;
import com.example.teampandanback.dto.file.response.FileDetailResponseDto;
import com.example.teampandanback.exception.ApiRequestException;
import com.example.teampandanback.utils.PandanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FileService {

    private final UserRepository userRepository;
    private final NoteRepository noteRepository;
    private final FileRepository fileRepository;
    private final UserProjectMappingRepository userProjectMappingRepository;
    private final PandanUtils pandanUtils;

    @Transactional
    public FileCreateResponseDto createFile(Long noteId, User currentUser, FileCreateRequestDto fileCreateRequestDto) {
        User user = userRepository.findById(currentUser.getUserId()).orElseThrow(
                () -> new ApiRequestException("등록되지 않은 유저의 접근입니다.")
        );

        Note note = noteRepository.findById(noteId).orElseThrow(
                () -> new ApiRequestException("생성되지 않은 노트입니다.")
        );

        Project connectedProject = Optional.ofNullable(note.getProject()).orElseThrow(
                () -> new ApiRequestException("연결된 프로젝트가 없습니다.")
        );
        UserProjectMapping userProjectMapping = userProjectMappingRepository.findByUserAndProject(user, connectedProject)
                .orElseThrow(
                        () -> new ApiRequestException("user와 project mapping을 찾지 못했습니다.")
                );

        List<FileDetailRequestDto> files = new ArrayList<>(fileCreateRequestDto.getFiles());
        files.stream()
                .map(file -> new File(file.getFileName(), file.getFileUrl(), user, note))
                .forEach(fileRepository::save);

        List<File> fileList = fileRepository.findFilesByNoteId(noteId);
        if (fileList.size() > pandanUtils.limitOfFile()) {
            throw new ApiRequestException(pandanUtils.messageForLimitOfFile());
        }

        List<FileDetailResponseDto> fileDetailResponseDtoList = new ArrayList<>();
        for (File file : fileList) {
            fileDetailResponseDtoList.add(FileDetailResponseDto.fromEntity(file));
        }

        return FileCreateResponseDto.builder()
                .files(fileDetailResponseDtoList)
                .build();

    }

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
