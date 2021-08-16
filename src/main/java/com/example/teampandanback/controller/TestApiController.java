package com.example.teampandanback.controller;

import com.example.teampandanback.OAuth2.UserDetailsImpl;
import com.example.teampandanback.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class TestApiController {

    private final UserRepository userRepository;
    @GetMapping("/api/test")
    public String apiTest(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userDetails.getUser().getEmail();
    }
}
