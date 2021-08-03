package com.example.teampandanback.domain.bookmark;

import com.example.teampandanback.domain.note.Note;
import com.example.teampandanback.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark,Long> {
    Optional<Bookmark> findByUserAndNote(User user, Note note);
}