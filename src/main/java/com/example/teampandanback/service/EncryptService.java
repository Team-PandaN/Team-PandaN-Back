package com.example.teampandanback.service;

import com.example.teampandanback.dto.project.StringEncryptResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Slf4j
@RequiredArgsConstructor
@Service
public class EncryptService {

    public StringEncryptResponseDto encodeString(Long projectId){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return StringEncryptResponseDto.of(bCryptPasswordEncoder.encode(projectId.toString()));
    }
}