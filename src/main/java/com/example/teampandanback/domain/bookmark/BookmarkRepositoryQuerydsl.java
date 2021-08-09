package com.example.teampandanback.domain.bookmark;

import com.example.teampandanback.dto.note.response.NoteEachBookmarkedResponseDto;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepositoryQuerydsl {
    void deleteByProjectId(long projectId);
    Optional<Bookmark> findByUserIdAndNoteId(Long userId, Long noteId);
    List<NoteEachBookmarkedResponseDto> findByUserId(Long noteId);
}
