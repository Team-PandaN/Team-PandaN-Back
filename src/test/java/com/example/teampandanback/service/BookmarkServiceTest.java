package com.example.teampandanback.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BookmarkServiceTest {

    @Test
    @DisplayName("Junit 성공 테스트")
    public void createProject(){
        assertThat(1).isEqualTo(1);
    }
}