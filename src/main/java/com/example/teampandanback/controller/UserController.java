package com.example.teampandanback.controller;

import com.example.teampandanback.dto.user.HeaderDto;
import com.example.teampandanback.dto.user.SignupRequestDto;
import com.example.teampandanback.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"로그인"})
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @ApiOperation(value = "카카오 소셜 로그인")
    @GetMapping("/user/kakao/callback")
    public HeaderDto kakaoLogin(@RequestParam(value = "code") String code) {
        return userService.kakaoLogin(code);
    }

    @ApiOperation(value = "기본 회원가입")
    @PostMapping("/user/signup")
    public void registerUser(@RequestBody SignupRequestDto requestDto) {
        userService.registerUser(requestDto);
    }

    @ApiOperation(value= "기본 로그인")
    @PostMapping("/user/login")
    public String login(@RequestBody SignupRequestDto requestDto) {
        return userService.login(requestDto);
    }
}
