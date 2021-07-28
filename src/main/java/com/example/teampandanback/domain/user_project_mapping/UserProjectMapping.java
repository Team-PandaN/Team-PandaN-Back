package com.example.teampandanback.domain.user_project_mapping;

import com.example.teampandanback.domain.project.Project;
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
    private Long seq;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Project project;

    @Builder
    public UserProjectMapping(Role role, User user, Project project) {
        this.role = role;
        this.user = user;
        this.project = project;
    }
}
