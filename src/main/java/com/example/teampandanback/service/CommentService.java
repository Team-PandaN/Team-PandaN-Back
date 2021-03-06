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

import java.util.Comparator;
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

    public CommentCreateResponseDto createComment(Long noteId, User currentUser, CommentCreateRequestDto commentCreateRequestDto) {
        User user = userRepository.findById(currentUser.getUserId()).orElseThrow(
                () -> new ApiRequestException("???????????? ?????? ????????? ???????????????.")
        );

        Note note = noteRepository.findById(noteId).orElseThrow(
                () -> new ApiRequestException("???????????? ?????? ???????????????.")
        );

        //??????
        Project connectedProject = Optional.ofNullable(note.getProject()).orElseThrow(
                () -> new ApiRequestException("????????? ??????????????? ????????????.")
        );

        //??????
        UserProjectMapping userProjectMapping = userProjectMappingRepository.findByUserAndProject(user, connectedProject)
                .orElseThrow(
                        () -> new ApiRequestException("user??? project mapping??? ?????? ???????????????.")
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

    public CommentReadListResponseDto readComments(Long noteId, User currentUser) {
        User user = userRepository.findById(currentUser.getUserId()).orElseThrow(
                () -> new ApiRequestException("???????????? ?????? ????????? ???????????????.")
        );

        Note note = noteRepository.findById(noteId).orElseThrow(
                () -> new ApiRequestException("???????????? ?????? ???????????????.")
        );

        //??????
        Project connectedProject = Optional.ofNullable(note.getProject()).orElseThrow(
                () -> new ApiRequestException("????????? ??????????????? ????????????.")
        );

        //??????
        UserProjectMapping userProjectMapping = userProjectMappingRepository.findByUserAndProject(user, connectedProject)
                .orElseThrow(
                        () -> new ApiRequestException("user??? project mapping??? ?????? ???????????????.")
                );

        List<Comment> commentList = commentRepository.findByNoteId(noteId);
        List<Comment> commentListSortedByCreatedAt = commentList.stream().sorted(Comparator.comparing(Comment::getCreatedAt)).collect(Collectors.toList());
        List<CommentReadEachResponseDto> commentReadEachResponseDtoList =
                commentListSortedByCreatedAt
                        .stream()
                        .map(e -> CommentReadEachResponseDto.fromEntity(e))
                        .collect(Collectors.toList());


        return CommentReadListResponseDto.builder()
                .commentList(commentReadEachResponseDtoList)
                .build();
    }

    // ?????? ??????
    @Transactional
    public CommentUpdateResponseDto updateComment(Long commentId, User currentUser, CommentUpdateRequestDto commentUpdateRequestDto) {

        Optional<Comment> maybeComment = commentRepository.findById(commentId);

        Comment updateComment = maybeComment
                                    .filter(c->c.getUser().getUserId().equals(currentUser.getUserId()))
                                    .map(c->c.update(commentUpdateRequestDto))
                                    .orElseThrow(() -> new ApiRequestException("????????? ????????? ????????? ??? ????????????"));

        return  CommentUpdateResponseDto.fromEntity(updateComment);
    }

    // ?????? ??????
    @Transactional
    public CommentDeleteResponseDto deleteComment(Long commentId, User currentUser) {
        commentRepository.deleteByCommentIdAndUserId(commentId, currentUser.getUserId());

        return CommentDeleteResponseDto.builder()
                .commentId(commentId)
                .build();

    }
}
