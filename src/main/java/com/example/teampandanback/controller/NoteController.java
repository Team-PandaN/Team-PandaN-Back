package com.example.teampandanback.controller;

import com.example.teampandanback.config.auth.LoginUser;
import com.example.teampandanback.dto.note.KanbanNoteSearchResponseDto;
import com.example.teampandanback.dto.note.NoteDeleteResponseDto;
import com.example.teampandanback.dto.note.NoteRequestDto;
import com.example.teampandanback.dto.note.NoteResponseDto;
import com.example.teampandanback.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/projects")
@RestController
public class NoteController {

    private final NoteService noteService;


    @GetMapping("/notes/{noteId}")
    public NoteResponseDto noteDetail (@PathVariable("noteId") Long noteId) {
        return noteService.readNoteDetail(noteId);
    }

    @PutMapping("/{noteId}")
    public NoteResponseDto updateNote (@PathVariable("noteId") Long noteId, @RequestBody NoteRequestDto noteRequestDto) {
        return noteService.updateNoteDetail(noteId, noteRequestDto);
        //  서비스의 메소드명은 변경될수있습니다.
    }

    @DeleteMapping("/{noteId}")
    public NoteDeleteResponseDto deleteNote (@PathVariable("noteId") Long noteId) {
        return noteService.deleteNote(noteId);
    }

    @GetMapping("/{projectId}/kanbans")
    public KanbanNoteSearchResponseDto kanbanNoteSearchResponse(@PathVariable("projectId") Long projectId){
        return noteService.readKanbanNote(projectId);
    }





}
