package com.example.teampandanback.OAuth2.Kakao;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class KakaoUserInfo {
    Long id;
    String email;
    String name;
    String picture;
}
