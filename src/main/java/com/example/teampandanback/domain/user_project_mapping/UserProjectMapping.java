package com.example.teampandanback.domain.user_project_mapping;

import com.example.teampandanback.domain.user.Role;
import com.example.teampandanback.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@DynamicUpdate
@Entity
public class UserProjectMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEQ")
    private Long seq;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "ROLE")
    private Role role;

    @ManyToOne
    @JoinColumn(nullable = false, name = "USER_ID")
    private User user;

    @ManyToOne
    @JoinColumn(nullable = false, name = "PROJECT_ID")
    private Project project;

    @Builder
    public UserProjectMapping(Role role, User user) {
        this.role = role;
        this.user = user;
        this.project = project;
    }
}
