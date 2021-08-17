package com.example.teampandanback.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@RequiredArgsConstructor
@Controller
public class LoginController {

    //회원 로그인 페이지
    @GetMapping("/kakaoTestLogin")
    public String index() {
        return "login";
    }
}
