package com.example.teampandanback.domain.user_project_mapping;

import com.example.teampandanback.domain.project.Project;
import com.example.teampandanback.domain.user.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserProjectMappingRepository extends JpaRepository<UserProjectMapping, Long>, UserProjectMappingRepositoryQuerydsl {

    UserProjectMapping findByUser_UserIdAndProject_ProjectId(Long userId, Long projectId); // but 갑자기 join함. 쿼리보면 갑분쪼

    void deleteByProject_ProjectId(Long projectId);

//    UserProjectMapping findByUser_IdAndProject_Id(Long userId, Long projectId);
//
//    void deleteByProject_Id(Long projectId);

    @Query("select upm from UserProjectMapping upm join fetch upm.user where upm.project = ?1")
    List<UserProjectMapping> findAllByProject(Project project);

    Optional<UserProjectMapping> findByUserAndProject(User newCrew, Project invitedProject);

    boolean existsByUserAndProject(User inviteOfferUser, Project inviteProject);
}
