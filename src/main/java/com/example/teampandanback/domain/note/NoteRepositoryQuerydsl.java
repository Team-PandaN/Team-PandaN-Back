package com.example.teampandanback.domain.note;

import java.util.List;

public interface NoteRepositoryQuerydsl {
    List<Note> findByProjectAndUser(Long projectId, Long userId);
}
