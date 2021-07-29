package com.example.teampandanback.controller;

import com.example.teampandanback.config.auth.LoginUser;
import com.example.teampandanback.dto.auth.SessionUser;
import com.example.teampandanback.dto.note.request.NoteCreateRequestDto;
import com.example.teampandanback.dto.note.request.NoteRequestDto;
import com.example.teampandanback.dto.note.response.*;
import com.example.teampandanback.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class NoteController {

    private final NoteService noteService;

    //노트 칸반형 조회
    @GetMapping("/projects/{projectId}/kanbans")
    public KanbanNoteSearchResponseDto kanbanNoteSearchResponse(@PathVariable("projectId") Long projectId){
        return noteService.readKanbanNote(projectId);
    }

    //노트 상세 조회
    @GetMapping("/notes/{noteId}")
    public NoteResponseDto noteDetail (@PathVariable("noteId") Long noteId) {
        return noteService.readNoteDetail(noteId);
    }

    // #4
    // What: 노트 생성 시 서버가 요청을 받을 PostMapping 함수를 만들었습니다.
    // Why: 노트 생성 시 requestbody를 받기 위해 Postmapping을 써야 합니다.
    // How: 프로젝트ID와 requestBody를 noteService.createNote 메소드에 넘겨주면 return 을 위한 NoteResponseDto를 전달받습니다.
    @PostMapping("/notes/{projectId}")
    public NoteCreateResponseDto createNote (@PathVariable Long projectId,
                                             @RequestBody NoteCreateRequestDto noteCreateRequestDto,
                                             @LoginUser SessionUser sessionUser){
        return noteService.createNote(projectId, noteCreateRequestDto, sessionUser);
    }

    //노트 수정
    @PutMapping("/notes/{noteId}")
    public NoteResponseDto updateNote (@PathVariable("noteId") Long noteId, @RequestBody NoteRequestDto noteRequestDto) {
        return noteService.updateNoteDetail(noteId, noteRequestDto);
        //  서비스의 메소드명은 변경될수있습니다.
    }

    //노트 삭제
    @DeleteMapping("/notes/{noteId}")
    public NoteDeleteResponseDto deleteNote (@PathVariable("noteId") Long noteId) {
        return noteService.deleteNote(noteId);
    }

    //노트 일반형 조회
    @GetMapping("/projects/{projectId}/issues")
    public NoteSearchResponseDto ordinaryNoteSearch(@PathVariable("projectId") Long projectId) {
        return noteService.readOrdinaryNote(projectId);
    }

}
