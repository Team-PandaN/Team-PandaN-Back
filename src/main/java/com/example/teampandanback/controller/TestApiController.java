package com.example.teampandanback.controller;

import com.example.teampandanback.OAuth2.UserDetailsImpl;
import com.example.teampandanback.domain.project.Project;
import com.example.teampandanback.domain.project.ProjectRepository;
import com.example.teampandanback.domain.user.User;
import com.example.teampandanback.domain.user.UserRepository;
import com.example.teampandanback.domain.user_project_mapping.UserProjectMapping;
import com.example.teampandanback.domain.user_project_mapping.UserProjectMappingRepository;
import com.example.teampandanback.domain.user_project_mapping.UserProjectRole;
import com.example.teampandanback.exception.ApiRequestException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(tags = {"테스트"})
@Slf4j
@RequiredArgsConstructor
@RestController
public class TestApiController {

    private final UserRepository userRepository;
    private final UserProjectMappingRepository userProjectMappingRepository;
    private final ProjectRepository projectRepository;

    @GetMapping("/api/test/not-logged-in")
    public Map<String,String> notLoggedIn(){
        Map<String,String> map = new HashMap<>();
        map.put("name","taegang");
        return map;
    }

    @ApiOperation(value = "로그인 테스트", notes = "토큰을 주면, 현재 로그인 되어있는 유저의 이름을 반환합니다.")
    @GetMapping("/api/test")
    public String apiTest(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userDetails.getUser().getName();
    }

    @ApiOperation(value = "무작위 프로젝트에 참가 시켜줌, 현재 탈퇴 기능 없으므로 딱 필요한 만큼만 호출바람, 프로젝트 10개 이상 생성 불가라는 조건 안걸어둠 여긴")
    @PostMapping("/api/test/invite")
    public String inviteTest(@AuthenticationPrincipal UserDetailsImpl userDetails){
        boolean isFinish = false;
        Long projectId = null;
        while(!isFinish) {
            //upperBound 125
            projectId = (long) (Math.random() * 125 + 1);
            User user = userDetails.getUser();
            Project project = projectRepository.findById(projectId).orElseThrow(
                    () -> new ApiRequestException("뭔가 문제가 발생함..")
            );
            UserProjectMapping userProjectMapping = userProjectMappingRepository.findByUserAndProject(user, project).orElse(null);
            if (userProjectMapping == null) {
                userProjectMappingRepository.save(UserProjectMapping.builder()
                        .userProjectRole(UserProjectRole.CREW)
                        .project(project)
                        .user(user)
                        .build());
                isFinish = true;
            }
        }

        return "Invited to Project: " + projectId;
    }
}
