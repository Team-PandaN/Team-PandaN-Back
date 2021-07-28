package com.example.teampandanback.dto.note;

import com.example.teampandanback.domain.note.Step;
import lombok.Getter;

import java.util.List;

@Getter
public class ProjectResponseDto {
    private final Step step;
    private final List<NoteResponseDto> notes;

    public ProjectResponseDto(Step step, List<NoteResponseDto> notes) {
        this.step = step;
        this.notes = notes;
    }

}
//    public ProjectResposeDto(String step,NoteRepository noteRepository) {
//        List<NoteResponseDto> noteResponseDtoList = new ArrayList<>();
//        for (NoteResponseDto noteResponseDto : noteRepository.findByProjectId()) {
//            noteResponseDtoList.add(noteResponseDto);
//        }
//        this.step = step;
//        this.notes = noteResponseDtoList;
//
//    }
}

