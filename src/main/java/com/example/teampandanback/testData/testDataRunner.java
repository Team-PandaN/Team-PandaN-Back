package com.example.teampandanback.testData;

import com.example.teampandanback.domain.user.Role;
import com.example.teampandanback.domain.user.User;
import com.example.teampandanback.domain.user.UserRepository;
import com.example.teampandanback.domain.user_project_mapping.UserProjectMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

@Component
public class testDataRunner implements ApplicationRunner {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserProjectMappingRepository userProjectMappingRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        User user = User.builder()
//                .email("Panda@naver.com")
//                .name("판다")
//                .role(Role.USER)
//                .build();
//        userRepository.save(user);
//
//        User user2 = User.builder()
//                .email("Tiger@google.com")
//                .name("호랑이")
//                .role(Role.USER)
//                .build();
//        userRepository.save(user2);
//
//        User user3 = User.builder()
//                .email("Chicken@kakao.com")
//                .name("닭")
//                .role(Role.USER)
//                .build();
//        userRepository.save(user3);
//
//        User user4 = User.builder()
//                .email("rat@slack.com")
//                .name("쥐")
//                .role(Role.USER)
//                .build();
//        userRepository.save(user4);
//
//        User user5 = User.builder()
//                .email("cow@kakao.com")
//                .name("소")
//                .role(Role.USER)
//                .build();
//        userRepository.save(user5);
//
//        User user6 = User.builder()
//                .email("dragon@kakao.com")
//                .name("용")
//                .role(Role.USER)
//                .build();
//        userRepository.save(user6);
//
//        User user7 = User.builder()
//                .email("horse@kakao.com")
//                .name("말")
//                .role(Role.USER)
//                .build();
//        userRepository.save(user7);
//
//        User user8 = User.builder()
//                .email("rhino@kakao.com")
//                .name("코뿔소")
//                .role(Role.USER)
//                .build();
//        userRepository.save(user8);
        //===========================================


    }
}
