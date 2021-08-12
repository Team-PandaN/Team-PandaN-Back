package com.example.teampandanback.domain.Comment;

public interface CommentRepositoryQuerydsl {
    void deleteByCommentIdAndUserId(Long commentId, Long userId);

    void deleteCommentByNoteId(Long noteId);

    void deleteCommentByProjectId(Long projectId);


}
