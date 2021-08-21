package com.example.teampandanback.domain.file;

import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface FileRepositoryQuerydsl {

    @Modifying(clearAutomatically = true)
    void deleteFileByNoteId(Long noteId);

    @Modifying(clearAutomatically = true)
    void deleteFileByProjectId(Long projectId);

    List<File> findFilesByNoteId(Long noteId);
}
