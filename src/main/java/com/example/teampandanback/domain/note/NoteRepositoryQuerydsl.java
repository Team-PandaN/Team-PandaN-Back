package com.example.teampandanback.domain.note;

import com.example.teampandanback.dto.note.response.NoteResponseDto;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

public interface NoteRepositoryQuerydsl {
    List<Note> findByProjectAndUser(Long projectId, Long userId);
    Optional<NoteResponseDto> findByNoteId(Long noteId);

    @Modifying(clearAutomatically = true)
    void deleteByProjectId(Long projectId);
}
