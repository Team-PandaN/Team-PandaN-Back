package com.example.teampandanback.domain.file;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;

import static com.example.teampandanback.domain.file.QFile.file;
import static com.example.teampandanback.domain.note.QNote.note;

public class FileRepositoryImpl implements FileRepositoryQuerydsl {
    private final JPAQueryFactory queryFactory;

    public FileRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);

    }

    @Override
    public void deleteFileByNoteId(Long noteId) {
        queryFactory
                .delete(file)
                .where(file.note.noteId.eq(noteId))
                .execute();
    }

    @Override
    public void deleteFileByProjectId(Long projectId) {

        queryFactory
                .delete(file)
                .where(file.note.noteId.in(
                        JPAExpressions
                                .select(note.noteId)
                                .from(note)
                                .where(note.project.projectId.eq(projectId))
                ))
                .execute();
    }

    @Override
    public List<File> findFilesByNoteId(Long noteId) {
        return queryFactory
                .select(file)
                .from(file)
                .where(file.note.noteId.eq(noteId))
                .fetch();
    }

    @Override
    public List<Long> findFileIdsByNoteId(Long noteId) {
        return queryFactory
                .select(file.fileId)
                .from(file)
                .where(file.note.noteId.eq(noteId))
                .fetch();
    }
}

