package com.example.teampandanback.domain.note;

import com.example.teampandanback.domain.project.QProject;
import com.example.teampandanback.dto.note.response.NoteResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.example.teampandanback.domain.note.QNote.note;
import static com.example.teampandanback.domain.project.QProject.project;

public class NoteRepositoryImpl implements NoteRepositoryQuerydsl{

    private final JPAQueryFactory queryFactory;

    public NoteRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Note> findByProjectAndUser(Long projectId, Long userId) {

        return queryFactory
                .select(note)
                .from(note)
                .where(note.project.projectId.eq(projectId).and(note.user.userId.eq(userId)))
                .fetch();
    }

    @Override
    public Optional<NoteResponseDto> findByNoteId(Long noteId){
        return Optional.ofNullable(
                queryFactory
                        .select(
                                Projections.constructor
                                        (NoteResponseDto.class,
                                                note.noteId,
                                                note.title,
                                                note.content,
                                                note.deadline,
                                                note.step,
                                                project.title))
                        .from(note)
                        .join(note.project, project)
                        .on(note.noteId.eq(noteId))
                        .fetchOne());
    }
}
