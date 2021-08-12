package com.example.teampandanback.domain.Comment;

import java.util.List;

public interface CommentRepositoryQuerydsl {

    List<Comment> findByNoteId(Long noteId);
}
