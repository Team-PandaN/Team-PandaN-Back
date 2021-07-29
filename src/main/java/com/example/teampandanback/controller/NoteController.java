package com.example.teampandanback.controller;

import com.example.teampandanback.dto.note.KanbanNoteSearchResponseDto;
import com.example.teampandanback.dto.note.NoteDeleteResponseDto;
import com.example.teampandanback.dto.note.NoteRequestDto;
import com.example.teampandanback.dto.note.NoteResponseDto;
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

    //노트 생성
    @PostMapping("/notes/{projectId}")
    public NoteResponseDto createNote (@PathVariable Long projectId, @RequestBody NoteRequestDto noteRequestDto){
        return noteService.createNote(projectId, noteRequestDto);
    }

    @GetMapping("/projects/{projectId}/issues")
    public NoteSerchResponseDto ordinaryNoteSerch(@PathVariable("projectId") Long projectId) {
        return noteService.readOrdinaryNote(projectId);
    }

}
