package com.example.teampandanback.domain.bookmark;

import com.example.teampandanback.domain.note.Note;
import com.example.teampandanback.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark,Long>, BookmarkRepositoryQuerydsl{
    Optional<Bookmark> findByUserAndNote(User user, Note note);
}
