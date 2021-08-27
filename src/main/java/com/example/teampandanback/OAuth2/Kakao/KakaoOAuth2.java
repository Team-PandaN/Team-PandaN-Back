package com.example.teampandanback.OAuth2.Kakao;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class KakaoOAuth2 {
    public KakaoUserInfo getUserInfo(String authorizedCode) {
        // 1. 인가코드 -> 액세스 토큰
        String accessToken = getAccessToken(authorizedCode);
        // 2. 액세스 토큰 -> 카카오 사용자 정보
        KakaoUserInfo userInfo = getUserInfoByToken(accessToken);

        return userInfo;
    }

    private String getAccessToken(String authorizedCode) {
        // HttpHeader 오브젝트 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HttpBody 오브젝트 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "c8dca36f643170e1873900b2b3d46e68");
        params.add("code", authorizedCode);


        //Switching

        //카카오에 요청을 보낼 때,
        //https://kauth.kakao.com/oauth/authorize?client_id=278001b9fc63c010f90080f4489dc776&redirect_uri=http://localhost:8080/user/kakao/callback&response_type=code
        //에서의 redirect_uri를 의미합니다. 보낸것과, 여기서 사용하는것이 같아야합니다.

        //프론트 로컬 용
//        params.add("redirect_uri", "http://localhost:3000/user/kakao/callback");

        //최종 배포용
//        params.add("redirect_uri", "http://front.blossomwhale.shop/user/kakao/callback");
        params.add("redirect_uri", "https://pandan.link/user/kakao/callback");
        //백엔드 로컬 테스트용
//        params.add("redirect_uri", "http://localhost:8080/user/kakao/callback");



        // HttpHeader와 HttpBody를 하나의 오브젝트에 담기
        RestTemplate rt = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(params, headers);

        // Http 요청하기 - Post방식으로 - 그리고 response 변수의 응답 받음.
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // JSON -> 액세스 토큰 파싱
        String tokenJson = response.getBody();
        JSONObject rjson = new JSONObject(tokenJson);
        String accessToken = rjson.getString("access_token");

        return accessToken;
    }


    private KakaoUserInfo getUserInfoByToken(String accessToken) {
        // HttpHeader 오브젝트 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HttpHeader와 HttpBody를 하나의 오브젝트에 담기
        RestTemplate rt = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);

        // Http 요청하기 - Post방식으로 - 그리고 response 변수의 응답 받음.
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        JSONObject body = new JSONObject(response.getBody());

        //null-safe한 값들.
        Long id = body.getLong("id");
        String name = body.getJSONObject("properties").getString("nickname");

        //null-safe하지 않은 값들.
        String email = "";
        if(body.getJSONObject("kakao_account").getBoolean("has_email")){
            if(!body.getJSONObject("kakao_account").getBoolean("email_needs_agreement"))
            email = body.getJSONObject("kakao_account").getString("email");
        }
        String picture = "https://s3.ap-northeast-2.amazonaws.com/front.blossomwhale.shop/ico-user.svg";
        if(!body.getJSONObject("kakao_account").getJSONObject("profile").getBoolean("is_default_image")){
            picture = body.getJSONObject("kakao_account").getJSONObject("profile").getString("profile_image_url");
        }

        return new KakaoUserInfo(id, email, name, picture);
    }
}
