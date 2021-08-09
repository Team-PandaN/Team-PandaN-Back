package com.example.teampandanback.domain.bookmark;

import java.util.Optional;

public interface BookmarkRepositoryQuerydsl {
    void deleteByProjectId(long projectId);
    Optional<Bookmark> findByUserIdAndNoteId(Long userId, Long noteId);
}
