package com.example.teampandanback.domain.user_project_mapping;

import com.example.teampandanback.domain.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserProjectMappingRepository extends JpaRepository<UserProjectMapping, Long> {

    UserProjectMapping findByUser_UserIdAndProject_ProjectId(Long userId, Long projectId); // but 갑자기 join함. 쿼리보면 갑분쪼
    Optional<UserProjectMapping> findByUserUserIdAndProjectProjectId(Long userId, Long projectId);

    List<UserProjectMapping> findByUser_UserId(Long userId);

    void deleteByProject_ProjectId(Long projectId);

//    UserProjectMapping findByUser_IdAndProject_Id(Long userId, Long projectId);
//
//    void deleteByProject_Id(Long projectId);

    @Query("select upm from UserProjectMapping upm join fetch upm.user")
    List<UserProjectMapping> findAllByProject(Project project);
}
