package com.example.teampandanback.service;

import com.example.teampandanback.OAuth2.Kakao.KakaoOAuth2;
import com.example.teampandanback.OAuth2.Kakao.KakaoUserInfo;
import com.example.teampandanback.OAuth2.UserDetailsImpl;
import com.example.teampandanback.domain.user.User;
import com.example.teampandanback.domain.user.UserRepository;
import com.example.teampandanback.domain.user_project_mapping.UserProjectMapping;
import com.example.teampandanback.domain.user_project_mapping.UserProjectMappingRepository;
import com.example.teampandanback.dto.user.HeaderDto;
import com.example.teampandanback.dto.user.SignupRequestDto;
import com.example.teampandanback.exception.ApiRequestException;
import com.example.teampandanback.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final KakaoOAuth2 kakaoOAuth2;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserProjectMappingRepository userProjectMappingRepository;

    @Value("${app.auth.tokenSecret}")
    private String secretKey;


    public HeaderDto kakaoLogin(String authorizedCode) {
        // 카카오 OAuth2 를 통해 카카오 사용자 정보 조회
        KakaoUserInfo userInfo = kakaoOAuth2.getUserInfo(authorizedCode);

        //nullable = false
        Long kakaoId = userInfo.getId();
        String name = userInfo.getName();
        String password = kakaoId + secretKey;

        //nullable = true
        String picture = userInfo.getPicture();
        String email = userInfo.getEmail();

        User kakaoUser = userRepository.findByKakaoId(kakaoId)
                .map(entity -> entity.update(name,picture)).orElse(User.builder()
                        .email(email)
                        .picture(picture)
                        .name(name)
                        .kakaoId(kakaoId)
                        .password(password)
                        .build());

        userRepository.save(kakaoUser);

        UserDetailsImpl userDetails = new UserDetailsImpl(kakaoUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        HeaderDto headerDto = new HeaderDto();
        headerDto.setTOKEN(jwtTokenProvider.createToken(Long.toString(kakaoUser.getUserId()), email, name, picture));
        return headerDto;
    }

    @Transactional
    public void registerUser(SignupRequestDto requestDto) {
        String name = requestDto.getUsername();
        User sameNameUser = userRepository.findByName(name).orElse(null);
        if(sameNameUser != null){
            throw new ApiRequestException("아이디가 중복됩니다.");
        }
        String password;
        password = passwordEncoder.encode(requestDto.getPassword());
        User user = User.builder()
                .name(name)
                .password(password)
                //TODO: 바꿔야함
                .picture("https://s3.ap-northeast-2.amazonaws.com/front.blossomwhale.shop/ico-user.svg")
                .build();
        userRepository.save(user);
    }

    @Transactional
    public String login(SignupRequestDto requestDto) {
        User user = userRepository.findByName(requestDto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 유저입니다."));
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
        return jwtTokenProvider.createToken(Long.toString(user.getUserId()), "", user.getName(), "");
    }

    //마지막 유저의 id
    @Transactional
    public Long getLastUserId(){
        User lastUser = userRepository.getLastUser().orElseThrow(
                ()-> new ApiRequestException("유저가 하나도 없습니다.")
        );

        return lastUser.getUserId();
    }

    // 이 유저는 얼마나 많은 프로젝트에 참여하였는가?
    @Transactional
    public Long getCountOfUserInvitedToProject(Long userId){
        return userProjectMappingRepository.getCountOfUserInvitedToProject(userId);
    }

    // 이 유저가 이 프로젝트에 참여 하였는가?
    @Transactional
    public Boolean isUserInvitedToProject(Long userId, Long projectId) {
        Optional<UserProjectMapping> optUserProjectMapping = userProjectMappingRepository.findByUserIdAndProjectId(userId, projectId);

        if (optUserProjectMapping.isPresent()) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }
}
