package com.example.teampandanback.domain.user_project_mapping;

import com.example.teampandanback.domain.project.Project;
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
    private UserProjectRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "USER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "PROJECT_ID")
    private Project project;

    @Builder
    public UserProjectMapping(UserProjectRole userProjectRole, User user, Project project ) {
        this.role = userProjectRole;
        this.user = user;
        this.project = project;
    }
}
