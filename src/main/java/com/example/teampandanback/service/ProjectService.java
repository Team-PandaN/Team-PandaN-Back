package com.example.teampandanback.service;

import com.example.teampandanback.domain.project.Project;
import com.example.teampandanback.domain.project.ProjectRepository;
import com.example.teampandanback.domain.user.User;
import com.example.teampandanback.domain.user.UserRepository;
import com.example.teampandanback.domain.user_project_mapping.UserProjectMapping;
import com.example.teampandanback.domain.user_project_mapping.UserProjectMappingRepository;
import com.example.teampandanback.domain.user_project_mapping.UserProjectRole;
import com.example.teampandanback.dto.auth.SessionUser;
import com.example.teampandanback.dto.project.*;
import com.example.teampandanback.exception.ApiRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final UserProjectMappingRepository userProjectMappingRepository;

    // Project 목록 조회
    @Transactional
    public List<ProjectResponseDto> readProjectList(SessionUser sessionUser){
        List<UserProjectMapping> userProjectMappingList = userProjectMappingRepository.findByUser_UserId(sessionUser.getUserId());

        return userProjectMappingList
                .stream()
                .map(userProjectMapping -> ProjectResponseDto.of(userProjectMapping.getProject()))
                .collect(Collectors.toList());
    }

    // Project 생성
    @Transactional
    public ProjectResponseDto createProject(ProjectRequestDto requestDto, SessionUser sessionUser){
        // 사용자 조회
        User user = userRepository.findById(sessionUser.getUserId())
                .orElseThrow(()-> new ApiRequestException("유저가 아니므로 프로젝트를 생성할 수 없습니다."));

        // 프로젝트 생성하고 저장
        Project project = projectRepository.save(Project.of(requestDto));

        // 유저-프로젝트 테이블에도 저장
        UserProjectMapping userProjectMapping = UserProjectMapping.builder()
                .userProjectRole(UserProjectRole.OWNER)
                .user(user)
                .project(project)
                .build();
        userProjectMappingRepository.save(userProjectMapping);

        return ProjectResponseDto.of(project);
    }

    // Project 수정
    @Transactional
    public ProjectResponseDto updateProject(Long projectId, ProjectRequestDto requestDto, SessionUser sessionUser){

        UserProjectMapping userProjectMapping = userProjectMappingRepository.findByUser_UserIdAndProject_ProjectId(sessionUser.getUserId(),projectId);

        if(!userProjectMapping.getRole().equals(UserProjectRole.OWNER)){
            throw new ApiRequestException("프로젝트 소유주가 아닙니다.");
        }

        Project project = userProjectMapping.getProject();
        project.update(requestDto);

        return ProjectResponseDto.of(project);
    }

    // Project 삭제
    @Transactional
    public ProjectDeleteResponseDto deleteProject(Long projectId, SessionUser sessionUser){
        UserProjectMapping userProjectMapping = userProjectMappingRepository.findByUser_UserIdAndProject_ProjectId(sessionUser.getUserId(),projectId);

        if(!userProjectMapping.getRole().equals(UserProjectRole.OWNER)){
            throw new ApiRequestException("프로젝트 소유주가 아닙니다.");
        }
        userProjectMappingRepository.deleteByProject_ProjectId(projectId);
        projectRepository.deleteById(projectId);

        return ProjectDeleteResponseDto.builder()
                .projectId(projectId)
                .build();
    }

    // Project 회원 조회
    @Transactional
    public ProjectCrewResponseDto readCrewList(Long projectId){
        // 프로젝트 조회
        Project project = projectRepository.findById(projectId)
                .orElseThrow(()-> new ApiRequestException("회원을 조회할 프로젝트가 존재하지 않습니다."));

        // 해당 프로젝트에 연관된 유저 목록 조회
        List<UserProjectMapping> userProjectMapping = userProjectMappingRepository.findAllByProject(project);
        // 유저목록에 있는 유저들의 아이디, 이름 조회
        List<CrewResponseDto> crewResponseDtoList = userProjectMapping.stream().map(
                                                            crew-> CrewResponseDto.builder()
                                                            .userId(crew.getUser().getUserId())
                                                            .userName(crew.getUser().getName())
                                                            .build())
                                                        .collect(Collectors.toList());

        return ProjectCrewResponseDto.builder()
                .crews(crewResponseDtoList)
                .projectId(projectId)
                .build();
    }

}
