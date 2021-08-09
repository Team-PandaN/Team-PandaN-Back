package com.example.teampandanback.domain.note;

import com.example.teampandanback.domain.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note,Long>, NoteRepositoryQuerydsl {
//    List<Note> findAllByProject(Long projectId);

    List<Note> findNoteByProject_projectId(Long projectId);
    // Project 에 연관된 Note 조회
    List<Note> findByProject(Project project);
    List<Note> findAllByProjectOrderByCreatedAtDesc(Project project);

    // Project 에 연관된 Note 삭제
    void deleteByProject_ProjectId(Long projectId);

    // 특정 Project 에서 내가 작성한 Note 조회
    // @Query("select n from Note n where n.project.projectId = :projectId and n.user.userId =:userId")
    // List<Note> findByProjectAndUser(Long projectId, Long userId);
}
