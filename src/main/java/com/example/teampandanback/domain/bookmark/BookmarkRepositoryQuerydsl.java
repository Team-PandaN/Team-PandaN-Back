package com.example.teampandanback.domain.bookmark;

import com.example.teampandanback.dto.note.response.NoteEachBookmarkedResponseDto;
import com.example.teampandanback.dto.note.response.NoteEachSearchInBookmarkResponseDto;
import com.example.teampandanback.utils.CustomPageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepositoryQuerydsl {

    @Modifying(clearAutomatically = true)
    void deleteByProjectId(long projectId);

    Optional<Bookmark> findByUserIdAndNoteId(Long userId, Long noteId);

    CustomPageImpl<NoteEachBookmarkedResponseDto> findNoteByUserIdInBookmark(Long userId, Pageable pageable);

    List<NoteEachSearchInBookmarkResponseDto> findNotesByUserIdAndKeywordInBookmarks(Long userId, List<String> keywordList);

    @Modifying(clearAutomatically = true)
    void deleteByNote(Long noteId);

    Long countCurrentUserBookmarkedAtByProjectId(Long userId, Long projectId);
}
