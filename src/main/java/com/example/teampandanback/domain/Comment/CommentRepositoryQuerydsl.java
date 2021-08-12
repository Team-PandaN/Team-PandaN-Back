package com.example.teampandanback.domain.Comment;

import java.util.List;

public interface CommentRepositoryQuerydsl {
    List<Comment> findByNoteId(Long noteId);

    void deleteByCommentIdAndUserId(Long commentId, Long userId);

    void deleteCommentByNoteId(Long noteId);

    void deleteCommentByProjectId(Long projectId);


}
