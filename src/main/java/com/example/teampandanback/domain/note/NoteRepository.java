package com.example.teampandanback.domain.note;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note,Long> {
//    List<Note> findAllByProject(Long projectId);

    List<Note> findNoteByProject_projectId(Long projectId);
    // Project 에 연관된 Note 삭제
    void deleteByProject_ProjectId(Long projectId);
}
