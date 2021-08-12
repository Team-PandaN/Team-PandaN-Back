package com.example.teampandanback.domain.note;

import com.example.teampandanback.dto.note.response.NoteEachMineInTotalResponseDto;
import com.example.teampandanback.dto.note.search.NoteEachSearchInTotalResponse;
import com.example.teampandanback.dto.note.response.NoteResponseDto;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

public interface NoteRepositoryQuerydsl {
    List<Note> findByProjectAndUser(Long projectId, Long userId);
    Optional<NoteResponseDto> findByNoteId(Long noteId);

    // 전체 프로젝트에서 해당 유저가 작성한 노트 조회
    List<NoteEachMineInTotalResponseDto> findUserNoteInTotalProject(Long userId); // findByUserId()
    @Modifying(clearAutomatically = true)
    void deleteByProjectId(Long projectId);

    List<Note> findAllNoteByProjectAndUserOrderByCreatedAtDesc(Long projectId, Long userId);
    List<NoteEachSearchInTotalResponse> findNotesByUserIdAndKeywordInTotal(Long userId, List<String> kewordList);
}
