package com.example.teampandanback.service;

import com.example.teampandanback.domain.bookmark.Bookmark;
import com.example.teampandanback.domain.bookmark.BookmarkRepository;
import com.example.teampandanback.domain.note.Note;
import com.example.teampandanback.domain.note.NoteRepository;
import com.example.teampandanback.domain.user.User;
import com.example.teampandanback.domain.user.UserRepository;
import com.example.teampandanback.dto.auth.SessionUser;
import com.example.teampandanback.exception.ApiRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BookmarkService {
    private final UserRepository userRepository;
    private final NoteRepository noteRepository;
    private final BookmarkRepository bookmarkRepository;

    public void bookmarkNote(Long noteId, SessionUser sessionUser) {

        //북마크 누른 사람
        User user = userRepository.findById(sessionUser.getUserId()).orElseThrow(
                ()->new ApiRequestException("등록되지 않은 유저의 접근입니다.")
        );

        //북마크 될 노트
        Note note = noteRepository.findById(noteId).orElseThrow(
                ()->new ApiRequestException("생성되지 않은 노트입니다.")
        );

        // 유저가 북마크를 했다는 레코드
       Bookmark bookmark = bookmarkRepository.findByUserAndNote(user,note)
               .orElseGet(()->Bookmark.builder()
               .user(user)
               .note(note)
               .build());

       bookmarkRepository.save(bookmark);
    }

    public void unBookmarkNote(Long noteId, SessionUser sessionUser) {

        //북마크 해제를 누른 사람
        User user = userRepository.findById(sessionUser.getUserId()).orElseThrow(
                ()->new ApiRequestException("등록되지 않은 유저의 접근입니다.")
        );

        //북마크가 해제 될 노트
        Note note = noteRepository.findById(noteId).orElseThrow(
                ()->new ApiRequestException("생성되지 않은 노트입니다.")
        );

        // 유저가 북마크를 했다는 레코드드
        Optional<Bookmark> bookmark = bookmarkRepository.findByUserAndNote(user,note);

        if(bookmark.isPresent()){
            bookmarkRepository.delete(bookmark.get());
        }

    }
}
