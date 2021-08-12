package com.example.teampandanback.service;


import com.example.teampandanback.domain.Comment.Comment;
import com.example.teampandanback.domain.Comment.CommentRepository;
import com.example.teampandanback.domain.note.Note;
import com.example.teampandanback.domain.note.NoteRepository;
import com.example.teampandanback.domain.user.User;
import com.example.teampandanback.domain.user.UserRepository;
import com.example.teampandanback.dto.auth.SessionUser;
import com.example.teampandanback.dto.comment.request.CommentCreateRequestDto;
import com.example.teampandanback.dto.comment.request.CommentUpdateRequestDto;
import com.example.teampandanback.dto.comment.response.CommentCreateResponseDto;
import com.example.teampandanback.dto.comment.response.CommentUpdateResponseDto;
import com.example.teampandanback.exception.ApiRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final UserRepository userRepository;
    private final NoteRepository noteRepository;
    private final CommentRepository commentRepository;

    public CommentCreateResponseDto createComment(Long noteId, SessionUser sessionUser, CommentCreateRequestDto commentCreateRequestDto){
        User user = userRepository.findById(sessionUser.getUserId()).orElseThrow(
                ()-> new ApiRequestException("등록되지 않은 유저의 접근입니다.")
        );

        Note note = noteRepository.findById(noteId).orElseThrow(
                () -> new ApiRequestException("생성되지 않은 노트입니다.")
        );

        Comment newComment = Comment.builder()
                .user(user)
                .note(note)
                .content(commentCreateRequestDto.getContent())
                .build();

        commentRepository.save(newComment);

        return CommentCreateResponseDto.builder()
                .content(newComment.getContent())
                .commentId(newComment.getCommentId())
                .writer(user.getName())
                .build();
    }

    // 댓글 수정
    @Transactional
    public CommentUpdateResponseDto updateComment(Long commentId, SessionUser sessionUser, CommentUpdateRequestDto commentUpdateRequestDto) {

        Optional<Comment> maybeComment = commentRepository.findById(commentId);

        Comment updateComment = maybeComment
                                    .filter(c->c.getUser().getUserId().equals(sessionUser.getUserId()))
                                    .map(c->c.update(commentUpdateRequestDto))
                                    .orElseThrow(() -> new ApiRequestException("댓글은 본인만 수정할 수 있습니다"));

        return  CommentUpdateResponseDto.fromEntity(updateComment);
    }
}
