package com.example.teampandanback.domain.bookmark;

import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface BookmarkRepositoryQuerydsl {

    @Modifying(clearAutomatically = true)
    void deleteByProjectId(long projectId);

    Optional<Bookmark> findByUserIdAndNoteId(Long userId, Long noteId);
}
