package com.example.teampandanback.domain.bookmark;

import com.example.teampandanback.dto.note.response.NoteEachBookmarkedResponseDto;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface BookmarkRepositoryQuerydsl {

    @Modifying(clearAutomatically = true)
    void deleteByProjectId(long projectId);

    Optional<Bookmark> findByUserIdAndNoteId(Long userId, Long noteId);
    List<NoteEachBookmarkedResponseDto> findByUserId(Long userId);

    @Modifying(clearAutomatically = true)
    void deleteByNote(Long noteId);
}
