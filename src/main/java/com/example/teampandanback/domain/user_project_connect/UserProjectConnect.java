//package com.example.teampandanback.domain.user_project_connect;
//
//import com.example.teampandanback.domain.user.Role;
//import com.example.teampandanback.domain.user.User;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import org.hibernate.annotations.DynamicUpdate;
//import org.hibernate.annotations.JoinColumnOrFormula;
//
//import javax.persistence.*;
//
//@Getter
//@NoArgsConstructor
//@DynamicUpdate
//@Entity
//public class UserProjectConnect {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private Role role;
//
//    @ManyToOne
//    @JoinColumn(nullable = false)
//    private User user;
//
//    @ManyToOne
//    @JoinColumn(nullable = false)
//    private Project project;
//
//    @Builder
//    public UserProjectConnect(Role role, User user, Project project) {
//        this.role = role;
//        this.user = user;
//        this.project = project;
//    }
//}
