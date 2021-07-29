package com.example.teampandanback.service;

import com.example.teampandanback.domain.note.Note;
import com.example.teampandanback.domain.note.NoteRepository;
import com.example.teampandanback.domain.note.Step;
import com.example.teampandanback.domain.project.Project;
import com.example.teampandanback.domain.project.ProjectRepository;
import com.example.teampandanback.dto.note.*;
import com.example.teampandanback.dto.project.ProjectResponseDto;
import com.example.teampandanback.exception.ApiRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class NoteService {

    private final NoteRepository noteRepository;

    @Transactional
    public NoteResponseDto readNoteDetail(Long noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(()-> new ApiRequestException("작성된 노트가 없습니다."));
        return NoteResponseDto.builder()
                .noteId(note.getNoteId())
                .title(note.getTitle())
                .content(note.getContent())
                .deadline(note.getDeadline())
                .build();
    }

    @Transactional
    public NoteResponseDto updateNoteDetail(Long noteId,NoteRequestDto noteRequestDto) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(()-> new ApiRequestException("수정 할 노트가 없습니다."));
        note.update(noteRequestDto);
        return NoteResponseDto.builder()
                .noteId(note.getNoteId())
                .title(note.getTitle())
                .content(note.getContent())
                .deadline(note.getDeadline())
                .build();
    }

    @Transactional
    public NoteDeleteResponseDto deleteNote(Long noteId) {
        noteRepository.deleteById(noteId);
        return NoteDeleteResponseDto.builder()
                .noteId(noteId)
                .build();
    }
    @Transactional
    public KanbanNoteSearchResponseDto readKanbanNote(Long projectId) {
        List<NoteOfProjectResponseDto> noteOfProjectResponseDtoList = new ArrayList<>();
        List<NoteResponseDto> noteResponseDtoList = new ArrayList<>();
        List<Note> noteLists = noteRepository.findByProjectId(projectId);
        for (Note note : noteRepository.findByProjectId(projectId)){
//            창고, 할것, 진행중, 끝
            for (Step stepType : Step.values()){
                if (note.getStep().equals(stepType)) {
                    noteResponseDtoList.add(NoteResponseDto.builder()
                            .noteId(note.getNoteId())
                            .title(note.getTitle())
                            .content(note.getContent())
                            .deadline(note.getDeadline())
                            .build());
                    noteOfProjectResponseDtoList.add(NoteOfProjectResponseDto.builder()
                            .step(note.getStep())
                            .noteResponseDtoList(noteResponseDtoList)
                            .build());
                }
            }
        }
        return KanbanNoteSearchResponseDto.builder()
                .noteOfProjectResponseDtoList(noteOfProjectResponseDtoList)
                .build();
    }



}


//    List<Note> noteList = noteRepository.findAll();
//        for (Note note : noteList){
//                if (note.getProject().getProjectId().equals(projectId)) {
//                for (Step stepType : Step.values()){
//                if (note.getStep().equals(stepType)) {
//                noteResponseDtoList.add(NoteResponseDto.builder()
//                .noteId(note.getNoteId())
//                .title(note.getTitle())
//                .content(note.getContent())
//                .deadline(note.getDeadline())
//                .build());
//                noteOfProjectResponseDtoList.add(NoteOfProjectResponseDto.builder()
//                .step(note.getStep())
//                .noteResponseDtoList(noteResponseDtoList)
//                .build());
//                }
//                }
//                }
////            창고, 할것, 진행중, 끝