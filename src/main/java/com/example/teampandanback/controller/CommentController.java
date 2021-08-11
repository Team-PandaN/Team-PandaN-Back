package com.example.teampandanback.controller;

import com.example.teampandanback.config.auth.LoginUser;
import com.example.teampandanback.dto.auth.SessionUser;
import com.example.teampandanback.dto.comment.request.CommentCreateRequestDto;
import com.example.teampandanback.dto.comment.response.CommentCreateResponseDto;
import com.example.teampandanback.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

    //코멘트 수정

    //코멘트 삭제
}
