package com.example.teampandanback.controller;

import com.example.teampandanback.OAuth2.UserDetailsImpl;
import com.example.teampandanback.domain.user.UserRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"테스트"})
@Slf4j
@RequiredArgsConstructor
@RestController
public class TestApiController {

    private final UserRepository userRepository;

    @ApiOperation(value = "로그인 테스트", notes = "토큰을 주면, 현재 로그인 되어있는 유저의 이름을 반환합니다.")
    @GetMapping("/api/test")
    public String apiTest(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userDetails.getUser().getName();
    }
}
