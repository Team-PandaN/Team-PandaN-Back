package com.example.teampandanback.domain.note;

import com.example.teampandanback.dto.note.response.NoteResponseDto;

import java.util.List;
import java.util.Optional;

public interface NoteRepositoryQuerydsl {
    List<Note> findByProjectAndUser(Long projectId, Long userId);
    Optional<NoteResponseDto> findByNoteId(Long noteId);
}
