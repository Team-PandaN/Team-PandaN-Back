package com.example.teampandanback.domain.bookmark;

import com.example.teampandanback.dto.note.response.NoteEachBookmarkedResponseDto;

import java.util.List;

import com.example.teampandanback.dto.note.response.NoteEachSearchInBookmarkResponseDto;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface BookmarkRepositoryQuerydsl {

    @Modifying(clearAutomatically = true)
    void deleteByProjectId(long projectId);

    Optional<Bookmark> findByUserIdAndNoteId(Long userId, Long noteId);
    List<NoteEachBookmarkedResponseDto> findNoteByUserIdInBookmark(Long userId);

    List<NoteEachSearchInBookmarkResponseDto> findNotesByUserIdAndKeywordInBookmarks(Long userId, List<String> keywordList);

    @Modifying(clearAutomatically = true)
    void deleteByNote(Long noteId);
}
