package com.example.teampandanback.domain.file;

import java.util.List;

public interface FileRepositoryQuerydsl {

    void deleteFileByNoteId(Long noteId);

    void deleteFileByProjectId(Long projectId);

    List<File> findFilesByNoteId(Long noteId);
}
