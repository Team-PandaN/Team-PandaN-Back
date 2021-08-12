package com.example.teampandanback.controller;

import com.example.teampandanback.config.auth.LoginUser;
import com.example.teampandanback.dto.auth.SessionUser;
import com.example.teampandanback.dto.comment.request.CommentCreateRequestDto;
import com.example.teampandanback.dto.comment.request.CommentUpdateRequestDto;
import com.example.teampandanback.dto.comment.response.CommentCreateResponseDto;
import com.example.teampandanback.dto.comment.response.CommentUpdateResponseDto;
import com.example.teampandanback.dto.comment.response.CommentDeleteResponseDto;
import com.example.teampandanback.dto.comment.response.CommentReadListResponseDto;
import com.example.teampandanback.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;

    //코멘트 작성
    @PostMapping("/api/comments/{noteId}")
    public CommentCreateResponseDto createComment(@PathVariable Long noteId, @LoginUser SessionUser sessionUser, @RequestBody CommentCreateRequestDto commentCreateRequestDto){
        return commentService.createComment(noteId,sessionUser,commentCreateRequestDto);
    }
    //코멘트 읽기
    @GetMapping("/api/comments/{noteId}")
    public CommentReadListResponseDto readComments(@PathVariable Long noteId, @LoginUser SessionUser sessionUser){
        return commentService.readComments(noteId,sessionUser);
    }
    //코멘트 수정
    @PutMapping("/api/comments/{commentId}")
    public CommentUpdateResponseDto updateComment(@PathVariable Long commentId, @LoginUser SessionUser sessionUser, @RequestBody CommentUpdateRequestDto commentUpdateRequestDto){
        return commentService.updateComment(commentId, sessionUser, commentUpdateRequestDto);
    }
    //코멘트 삭제
    @DeleteMapping("/api/comments/{commentId}")
    public CommentDeleteResponseDto deleteComment(@PathVariable Long commentId, @LoginUser SessionUser sessionUser) {
        return commentService.deleteComment(commentId, sessionUser);
    }
}
