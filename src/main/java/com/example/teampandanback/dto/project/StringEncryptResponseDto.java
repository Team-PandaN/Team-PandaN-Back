package com.example.teampandanback.dto.project;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Getter
public class StringEncryptResponseDto {

    private String encryptedString;

    @Builder
    public StringEncryptResponseDto ( String encryptedString) {
        this.encryptedString = encryptedString;
    }

    public static StringEncryptResponseDto of (String encryptedString) {
        return new StringEncryptResponseDto(encryptedString);
    }

}
