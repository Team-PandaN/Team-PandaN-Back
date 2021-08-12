package com.example.teampandanback.controller;

import com.example.teampandanback.config.auth.LoginUser;
import com.example.teampandanback.dto.auth.SessionUser;
import com.example.teampandanback.dto.note.request.NoteCreateRequestDto;
import com.example.teampandanback.dto.note.request.NoteUpdateRequestDto;
import com.example.teampandanback.dto.note.response.*;
import com.example.teampandanback.dto.note.response.NoteSearchInTotalResponseDto;
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

    //내가 쓴 노트 조회
    @GetMapping("/projects/{projectId}/mynotes")
    public NoteMineInProjectResponseDto readNotesMineOnly(@PathVariable("projectId") Long projectId, @LoginUser SessionUser sessionUser) {
        return noteService.readNotesMineOnly(projectId, sessionUser);
    }

    //내가 북마크한 노트 조회
    @GetMapping("/notes/mybookmarks")
    public NoteBookmarkedResponseDto  readBookmarkedMine(@LoginUser SessionUser sessionUser) {
        return noteService.readBookmarkedMine(sessionUser);
    }

    //노트 상세 조회
    @GetMapping("/notes/{noteId}")
    public NoteResponseDto noteDetail (@PathVariable("noteId") Long noteId, @LoginUser SessionUser sessionUser) {
        return noteService.readNoteDetail(noteId, sessionUser);
    }

    //내가 생성
    @PostMapping("/notes/{projectId}")
    public NoteCreateResponseDto createNote (@PathVariable Long projectId,
                                             @RequestBody NoteCreateRequestDto noteCreateRequestDto,
                                             @LoginUser SessionUser sessionUser){
        return noteService.createNote(projectId, noteCreateRequestDto, sessionUser);
    }

    //노트 수정
    @PutMapping("/notes/{noteId}")
    public NoteUpdateResponseDto updateNote (@PathVariable("noteId") Long noteId, @RequestBody NoteUpdateRequestDto noteUpdateRequestDto) {
        return noteService.updateNoteDetail(noteId, noteUpdateRequestDto);
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

    // 전체 프로젝트에서 내가 작성한 노트 조회
    @GetMapping("/notes/mynotes")
    public NoteMineInTotalResponseDto readMyNoteInTotalProject(@LoginUser SessionUser sessionUser){
        return noteService.readMyNoteInTotalProject(sessionUser);
    }

    // 사용자가 멤버인 프로젝트들 중에서 노트 제목 검색
    @GetMapping("/notes/search")
    public NoteSearchInTotalResponseDto searchNoteInMyProjects(@LoginUser SessionUser sessionUser, @RequestParam("keyword") String rawKeyword){
        return noteService.searchNoteInMyProjects(sessionUser, rawKeyword);
    }

    // 내가 쓴 문서들 중에서 노트 제목 검색
    @GetMapping("/notes/search/mynotes")
    public NoteSearchInMineResponseDto searchNoteInMyNotes(@LoginUser SessionUser sessionUser, @RequestParam("keyword") String rawKeyword){
        return noteService.searchNoteInMyNotes(sessionUser, rawKeyword);
    }
}
