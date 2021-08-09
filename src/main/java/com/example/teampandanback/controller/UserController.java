package com.example.teampandanback.controller;

import com.example.teampandanback.config.auth.LoginUser;
import com.example.teampandanback.dto.auth.SessionUser;
import com.example.teampandanback.dto.user.UserDetailResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {


    // 로그인한 회원의 정보 조회
    @GetMapping("/detail")
    public UserDetailResponseDto readUserDetail(@LoginUser SessionUser sessionUser){

        return UserDetailResponseDto.builder()
                    .email(sessionUser.getEmail())
                    .name(sessionUser.getName())
                    .picture(sessionUser.getPicture())
                    .build();
    }

}
