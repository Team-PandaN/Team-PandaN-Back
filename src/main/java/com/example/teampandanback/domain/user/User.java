package com.example.teampandanback.domain.user;

import com.example.teampandanback.domain.Timestamped;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@DynamicUpdate
@Entity
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long userId;

    @Column(nullable = false, name = "NAME")
    private String name;

    @Column(nullable = true, name = "EMAIL")
    private String email;

    @Column(nullable = true, name = "PICTURE")
    private String picture;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private Long kakaoId;

    @Builder
    public User(String name, String email, String picture, String password, Long kakaoId) {
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.password = password;
        this.kakaoId = kakaoId;
    }

    public User update(String name, String picture){
        this.name = name;
        this.picture = name;

        return this;
    }

}
