package com.example.teampandanback.service;

import com.example.teampandanback.domain.note.Note;
import com.example.teampandanback.domain.note.NoteRepository;
import com.example.teampandanback.exception.ApiRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class LockManagerService {
    private final NoteRepository noteRepository;

    @PersistenceContext
    EntityManager em;

    @Transactional
    public void preProcess(Long noteId){
        em.clear();
        Note note = noteRepository.findById(noteId).orElseThrow(
                () -> new ApiRequestException("해당 노트가 없습니다.")
        );
//        note.setLocked(true);
        note.setWriting(true);
    }

    @Transactional
    public void deLock(Long noteId){
        em.clear();
        Note note = noteRepository.findById(noteId).orElseThrow(
                ()-> new ApiRequestException("해당 노트가 없습니다.")
        );
        note.setLocked(false);
        note.setWriting(false);
        note.setWriterId(null);
    }

    @Transactional
    public Boolean isAnyoneWriting(Long noteId){
        em.clear();
        Note note = noteRepository.findById(noteId).orElseThrow(
                ()-> new ApiRequestException("해당 노트가 없습니다.")
        );
        if(note.getWriting() == true){
            return true;
        }else{
            return false;
        }
    }

    @Transactional
    public void assumeThatNobodyIsWriting(Long noteId){
        em.clear();
        Note note = noteRepository.findById(noteId).orElseThrow(
                ()-> new ApiRequestException("해당 노트가 없습니다.")
        );
        note.setWriting(false);
    }
}
