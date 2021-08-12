package com.example.teampandanback.service;

import com.example.teampandanback.domain.Comment.CommentRepository;
import com.example.teampandanback.domain.bookmark.BookmarkRepository;
import com.example.teampandanback.domain.note.NoteRepository;
import com.example.teampandanback.domain.project.Project;
import com.example.teampandanback.domain.project.ProjectRepository;
import com.example.teampandanback.domain.user.User;
import com.example.teampandanback.domain.user.UserRepository;
import com.example.teampandanback.domain.user_project_mapping.UserProjectMapping;
import com.example.teampandanback.domain.user_project_mapping.UserProjectMappingRepository;
import com.example.teampandanback.domain.user_project_mapping.UserProjectRole;
import com.example.teampandanback.dto.auth.SessionUser;
import com.example.teampandanback.dto.project.request.ProjectInvitedRequestDto;
import com.example.teampandanback.dto.project.request.ProjectRequestDto;
import com.example.teampandanback.dto.project.request.ProjectResponseDto;
import com.example.teampandanback.dto.project.response.*;
import com.example.teampandanback.exception.ApiRequestException;
import com.example.teampandanback.utils.AESEncryptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
@Slf4j
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final UserProjectMappingRepository userProjectMappingRepository;
    private final NoteRepository noteRepository;
    private final BookmarkRepository bookmarkRepository;
    private final CommentRepository commentRepository;
    private final AESEncryptor aesEncryptor;

    // Project 목록 조회
    @Transactional
    public List<ProjectResponseDto> readProjectList(SessionUser sessionUser) {
        List<ProjectResponseDto> projectResponseDtoList = userProjectMappingRepository
                .findProjectByUser_UserId(sessionUser.getUserId());

        return projectResponseDtoList;

    }

    // Project 생성
    @Transactional
    public ProjectResponseDto createProject(ProjectRequestDto requestDto, SessionUser sessionUser) {
        // 사용자 조회
        User user = userRepository.findById(sessionUser.getUserId())
                .orElseThrow(() -> new ApiRequestException("유저가 아니므로 프로젝트를 생성할 수 없습니다."));

        // 프로젝트 생성하고 저장
        Project project = projectRepository.save(Project.toEntity(requestDto));

        // 유저-프로젝트 테이블에도 저장
        UserProjectMapping userProjectMapping = UserProjectMapping.builder()
                .userProjectRole(UserProjectRole.OWNER)
                .user(user)
                .project(project)
                .build();
        userProjectMappingRepository.save(userProjectMapping);

        return ProjectResponseDto.fromEntity(project);
    }

    // Project 수정
    @Transactional
    public ProjectDetailResponseDto updateProject(Long projectId, ProjectRequestDto requestDto, SessionUser sessionUser) {

        UserProjectMapping userProjectMapping = userProjectMappingRepository.findByUserIdAndProjectIdJoin(sessionUser.getUserId(), projectId);

        if (!userProjectMapping.getRole().equals(UserProjectRole.OWNER)) {
            throw new ApiRequestException("프로젝트 소유주가 아닙니다.");
        }
        Project project = userProjectMapping.getProject();
        project.update(requestDto);

        ProjectDetailResponseDto responseDto = ProjectDetailResponseDto.builder()
                .projectId(project.getProjectId())
                .detail(project.getDetail())
                .title(project.getTitle())
                .role(userProjectMapping.getRole())
                .build();

        responseDto.updateCrewCount(userProjectMappingRepository.findCountProjectMember(project.getProjectId()));

        return responseDto;
    }

    // Project 삭제
    @Transactional
    public ProjectDeleteResponseDto deleteProject(Long projectId, SessionUser sessionUser) {
        Optional<UserProjectMapping> userProjectMapping = userProjectMappingRepository.findByUserIdAndProjectId(sessionUser.getUserId(), projectId);

        // Project의 OWNER 권한 확인
        if (!userProjectMapping.isPresent()) { //   우선 해당 프로젝트의 CREW이기라도 한지.
            throw new ApiRequestException("프로젝트 소유주가 아닙니다.");
        } else if (!userProjectMapping.get().getRole().equals(UserProjectRole.OWNER)) { //   우선 해당 프로젝트의 CREW이더라도, OWNER가 아닌지,
            throw new ApiRequestException("프로젝트 소유주가 아닙니다.");
        }

        // 해당 Project 와 연관된 Note에 속한 코멘트 삭제
        commentRepository.deleteCommentByProjectId(projectId);

        // Bookmark 테이블에서 Note 와 연관된 북마크 삭제
        bookmarkRepository.deleteByProjectId(projectId);

        // 해당 Project 와 연관된 Note 삭제
        noteRepository.deleteByProjectId(projectId);

        // UserProjectMapping 테이블에서 삭제
        userProjectMappingRepository.deleteByProjectId(projectId);

        // Project 테이블에서 Project 삭제
        projectRepository.deleteById(projectId);

        return ProjectDeleteResponseDto.builder()
                .projectId(projectId)
                .build();
    }

    // Project 회원 조회
    @Transactional
    public ProjectCrewResponseDto readCrewList(Long projectId) {
        // 프로젝트 조회
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApiRequestException("회원을 조회할 프로젝트가 존재하지 않습니다."));

        // 해당 프로젝트에 연관된 유저 목록 조회
        List<UserProjectMapping> userProjectMapping = userProjectMappingRepository.findAllByProject(project);
        // 유저목록에 있는 유저들의 아이디, 이름 조회
        List<CrewResponseDto> crewResponseDtoList = userProjectMapping.stream().map(
                crew -> CrewResponseDto.builder()
                        .userId(crew.getUser().getUserId())
                        .userName(crew.getUser().getName())
                        .build())
                .collect(Collectors.toList());

        return ProjectCrewResponseDto.builder()
                .crews(crewResponseDtoList)
                .projectId(projectId)
                .build();
    }

    @Transactional
    public ProjectInvitedResponseDto invitedProject(ProjectInvitedRequestDto projectInvitedRequestDto, SessionUser sessionUser) {

        String decodedString = null;
        Long decodedLong = null;
        try {
            decodedString = aesEncryptor.decrypt(projectInvitedRequestDto.getInviteCode());
            decodedLong = Long.parseLong(decodedString);
        } catch (NumberFormatException e) {
            log.info("복호화 된 프로젝트ID가 숫자가 아닙니다. 프로젝트ID = " + decodedString);
            throw new ApiRequestException("유효하지 않은 초대 코드입니다.");
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new ApiRequestException("유효하지 않은 초대 코드 입니다.");
        }

        User newCrew = userRepository.findById(sessionUser.getUserId()).orElseThrow(
                () -> new ApiRequestException("등록되지 않은 유저의 접근입니다.")
        );

        Project invitedProject = projectRepository.findById(decodedLong).orElseThrow(
                () -> new ApiRequestException("생성되지 않은 프로젝트입니다.")
        );

        UserProjectMapping newCrewRecord = userProjectMappingRepository.findByUserAndProject(newCrew, invitedProject)
                .orElseGet(() -> UserProjectMapping.builder()
                        .userProjectRole(UserProjectRole.CREW)
                        .user(newCrew)
                        .project(invitedProject)
                        .build());

        userProjectMappingRepository.save(newCrewRecord);

        return ProjectInvitedResponseDto.builder()
                .projectId(decodedLong)
                .build();
    }

    // Project 초대코드 생성
    public ProjectInviteResponseDto inviteProject(Long projectId, SessionUser sessionUser) {
        User inviteOfferUser = userRepository.findById(sessionUser.getUserId()).orElseThrow(
                () -> new ApiRequestException("등록되지 않은 유저의 접근입니다.")
        );

        Project inviteProject = projectRepository.findById(projectId).orElseThrow(
                () -> new ApiRequestException("생성되지 않은 프로젝트입니다.")
        );

        boolean isUserIsMemberOfProject = userProjectMappingRepository.existsByUserAndProject(inviteOfferUser, inviteProject);

        if (!isUserIsMemberOfProject) {
            throw new ApiRequestException("유저가 프로젝트의 구성원이 아닌데, 초대코드를 생성하려 합니다.");
        }

        String encodedString = null;
        try {
            encodedString = aesEncryptor.encrypt(Long.toString(projectId));
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new ApiRequestException("초대 코드 발급 중 오류가 발생하였습니다.");
        }

        return ProjectInviteResponseDto.builder()
                .inviteCode(encodedString)
                .build();
    }

    // Project 상세 조회
    @Transactional(readOnly = true)
    public ProjectDetailResponseDto readProjectDetail(SessionUser sessionUser, Long projectId) {

        ProjectDetailResponseDto responseDto = userProjectMappingRepository
                .findProjectDetail(sessionUser.getUserId(), projectId)
                .orElseThrow(() -> new ApiRequestException("해당 유저는 접근권한이 없는 프로젝트입니다."));

        responseDto.updateCrewCount(userProjectMappingRepository.findCountProjectMember(projectId));

        return responseDto;
    }

    // 사이드 바에 들어갈 Project 목록 조회(최대 5개)
    @Transactional(readOnly = true)
    public List<ProjectSidebarResponseDto> readProjectListSidebar(SessionUser sessionUser) {
        Long readSize = 5L;

        return userProjectMappingRepository.findProjectListTopSize(sessionUser.getUserId(), readSize);
    }
}
