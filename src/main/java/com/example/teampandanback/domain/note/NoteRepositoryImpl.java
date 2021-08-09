package com.example.teampandanback.domain.note;

import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;

import static com.example.teampandanback.domain.note.QNote.note;

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
}
