package com.example.teampandanback.domain.note;

import com.example.teampandanback.domain.project.Project;

import com.example.teampandanback.dto.note.response.NoteEachMineInTotalResponseDto;
import com.example.teampandanback.dto.note.response.NoteEachSearchInMineResponseDto;
import com.example.teampandanback.dto.note.response.NoteResponseDto;
import com.example.teampandanback.dto.note.response.noteEachSearchInTotalResponseDto;
import com.example.teampandanback.utils.CustomPageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

public interface NoteRepositoryQuerydsl {
    List<Note> findByProjectAndUser(Long projectId, Long userId);

    Optional<NoteResponseDto> findByNoteId(Long noteId);

    // 전체 프로젝트에서 해당 유저가 작성한 노트 조회
    CustomPageImpl<NoteEachMineInTotalResponseDto> findUserNoteInTotalProject(Long userId, Pageable pageable, List<Long> projectIdList); // findByUserId()

    @Modifying(clearAutomatically = true)
    void deleteByProjectId(Long projectId);

    // 해당 Project 에서 유저가 작성한 Note 조회
    CustomPageImpl<Note> findAllNoteByProjectAndUserOrderByCreatedAtDesc(Long projectId, Long userId, Pageable pageable);

    List<noteEachSearchInTotalResponseDto> findNotesByUserIdAndKeywordInTotal(Long userId, List<String> keywordList);

    // 내가 작성한 문서들 중에서 제목으로 노트 검색
    List<NoteEachSearchInMineResponseDto> findNotesByUserIdAndKeywordInMine(Long userId, List<String> keywordList, List<Long> projectIdList);

    List<Note> findNotesByNoteIdList(List<Long> noteIdList);

    CustomPageImpl<Note> findAllByProjectOrderByCreatedAtDesc(Project project, Pageable pageable);

    List<Note> findAllByProjectId(Long projectId);

    Long countByProjectId(Long projectId);
}
