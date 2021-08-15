package com.example.teampandanback.controller;

import com.example.teampandanback.dto.user.HeaderDto;
import com.example.teampandanback.dto.user.UserDetailResponseDto;
import com.example.teampandanback.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/user/kakao/callback")
    public HeaderDto kakaoLogin(String code){
        return userService.kakaoLogin(code);
    }

}
