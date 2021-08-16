package com.example.teampandanback.service;

import com.example.teampandanback.OAuth2.Kakao.KakaoOAuth2;
import com.example.teampandanback.OAuth2.Kakao.KakaoUserInfo;
import com.example.teampandanback.OAuth2.UserDetailsImpl;
import com.example.teampandanback.domain.user.User;
import com.example.teampandanback.domain.user.UserRepository;
import com.example.teampandanback.dto.user.HeaderDto;
import com.example.teampandanback.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final KakaoOAuth2 kakaoOAuth2;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;


    public HeaderDto kakaoLogin(String authorizedCode) {
        // 카카오 OAuth2 를 통해 카카오 사용자 정보 조회
        KakaoUserInfo userInfo = kakaoOAuth2.getUserInfo(authorizedCode);
        String name = userInfo.getName();
        String picture = userInfo.getPicture();
        String email = userInfo.getEmail();


        User user = userRepository.findByEmail(email)
                .map(entity -> entity.update(name,picture)).orElse(User.builder()
                        .email(email)
                        .picture(picture)
                        .name(name)
                        .build());

        userRepository.save(user);

        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        HeaderDto headerDto = new HeaderDto();
        headerDto.setTOKEN(jwtTokenProvider.createToken(email, email, name, picture));
        return headerDto;
    }
}
