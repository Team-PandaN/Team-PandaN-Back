package com.example.teampandanback.domain.user_project_mapping;

import com.example.teampandanback.domain.project.Project;
import com.example.teampandanback.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserProjectMappingRepository extends JpaRepository<UserProjectMapping, Long>, UserProjectMappingRepositoryQuerydsl {

    @Query("select upm from UserProjectMapping upm join fetch upm.user where upm.project = :project")
    List<UserProjectMapping> findAllByProject(@Param("project") Project project);

    Optional<UserProjectMapping> findByUserAndProject(User newCrew, Project invitedProject);

    boolean existsByUserAndProject(User inviteOfferUser, Project inviteProject);

    @Query("select upm from UserProjectMapping upm where upm.project.projectId = :projectId")
    List<UserProjectMapping> findByProject_ProjectId(@Param("projectId") Long projectId);
}
