package com.example.teampandanback.service;


import com.example.teampandanback.domain.Comment.Comment;
import com.example.teampandanback.domain.Comment.CommentRepository;
import com.example.teampandanback.domain.note.Note;
import com.example.teampandanback.domain.note.NoteRepository;
import com.example.teampandanback.domain.project.Project;
import com.example.teampandanback.domain.user.User;
import com.example.teampandanback.domain.user.UserRepository;
import com.example.teampandanback.domain.user_project_mapping.UserProjectMapping;
import com.example.teampandanback.domain.user_project_mapping.UserProjectMappingRepository;
import com.example.teampandanback.dto.auth.SessionUser;
import com.example.teampandanback.dto.comment.request.CommentCreateRequestDto;
import com.example.teampandanback.dto.comment.request.CommentUpdateRequestDto;
import com.example.teampandanback.dto.comment.response.CommentCreateResponseDto;
import com.example.teampandanback.dto.comment.response.CommentUpdateResponseDto;
import com.example.teampandanback.dto.comment.response.CommentDeleteResponseDto;
import com.example.teampandanback.dto.comment.response.CommentReadEachResponseDto;
import com.example.teampandanback.dto.comment.response.CommentReadListResponseDto;
import com.example.teampandanback.exception.ApiRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final UserRepository userRepository;
    private final NoteRepository noteRepository;
    private final CommentRepository commentRepository;
    private final UserProjectMappingRepository userProjectMappingRepository;

    public CommentCreateResponseDto createComment(Long noteId, SessionUser sessionUser, CommentCreateRequestDto commentCreateRequestDto) {
        User user = userRepository.findById(sessionUser.getUserId()).orElseThrow(
                () -> new ApiRequestException("등록되지 않은 유저의 접근입니다.")
        );

        Note note = noteRepository.findById(noteId).orElseThrow(
                () -> new ApiRequestException("생성되지 않은 노트입니다.")
        );

        //쿼리
        Project connectedProject = Optional.ofNullable(note.getProject()).orElseThrow(
                () -> new ApiRequestException("연결된 프로젝트가 없습니다.")
        );

        //쿼리
        UserProjectMapping userProjectMapping = userProjectMappingRepository.findByUserAndProject(user, connectedProject)
                .orElseThrow(
                        () -> new ApiRequestException("user와 project mapping을 찾지 못했습니다.")
                );

        Comment newComment = Comment.builder()
                .user(user)
                .note(note)
                .content(commentCreateRequestDto.getContent())
                .build();

        Comment savedComment = commentRepository.save(newComment);

        return CommentCreateResponseDto.builder()
                .content(savedComment.getContent())
                .commentId(savedComment.getCommentId())
                .writer(savedComment.getUser().getName())
                .build();
    }

    public CommentReadListResponseDto readComments(Long noteId, SessionUser sessionUser) {
        User user = userRepository.findById(sessionUser.getUserId()).orElseThrow(
                () -> new ApiRequestException("등록되지 않은 유저의 접근입니다.")
        );

        Note note = noteRepository.findById(noteId).orElseThrow(
                () -> new ApiRequestException("생성되지 않은 노트입니다.")
        );

        //쿼리
        Project connectedProject = Optional.ofNullable(note.getProject()).orElseThrow(
                () -> new ApiRequestException("연결된 프로젝트가 없습니다.")
        );

        //쿼리
        UserProjectMapping userProjectMapping = userProjectMappingRepository.findByUserAndProject(user, connectedProject)
                .orElseThrow(
                        () -> new ApiRequestException("user와 project mapping을 찾지 못했습니다.")
                );

        List<Comment> commentList = commentRepository.findByNoteId(noteId);
        List<CommentReadEachResponseDto> commentReadEachResponseDtoList =
                commentList
                        .stream()
                        .map(e -> CommentReadEachResponseDto.fromEntity(e))
                        .collect(Collectors.toList());


        return CommentReadListResponseDto.builder()
                .commentList(commentReadEachResponseDtoList)
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

    // 댓글 삭제
    @Transactional
    public CommentDeleteResponseDto deleteComment(Long commentId, SessionUser sessionUser) {
        commentRepository.deleteByCommentIdAndUserId(commentId, sessionUser.getUserId());

        return CommentDeleteResponseDto.builder()
                .commentId(commentId)
                .build();

    }
}
