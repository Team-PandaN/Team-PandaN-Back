package com.example.teampandanback.domain.Comment;

public interface CommentRepositoryQuerydsl {
    void deleteByCommentIdAndUserId(Long commentId, Long userId);

    Comment findByCommentIdAndUserId(Long commentId, Long userId);

    void deleteCommentByNoteId(Long noteId);

    void deleteCommentByProjectId(Long projectId);


}
