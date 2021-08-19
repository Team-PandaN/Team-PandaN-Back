package com.example.teampandanback.dto.note.response;

import com.example.teampandanback.utils.CustomPageImpl;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class NoteBookmarkedResponseDto {
    private List<NoteEachBookmarkedResponseDto> noteList;
    private int totalPages;
    private long totalElements;
    private int pageNumber;
    private boolean first;
    private boolean last;

    @Builder
    public NoteBookmarkedResponseDto(List<NoteEachBookmarkedResponseDto> noteList, int totalPages, long totalElements, int pageNumber, boolean first, boolean last) {
        this.noteList = noteList;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.pageNumber = pageNumber;
        this.first = first;
        this.last = last;
    }

    public static NoteBookmarkedResponseDto fromEntity(List<NoteEachBookmarkedResponseDto> noteList, CustomPageImpl customPage) {
        return NoteBookmarkedResponseDto.builder()
                .noteList(noteList)
                .totalPages(customPage.getTotalPages())
                .totalElements(customPage.getTotalElements())
                .pageNumber(customPage.getNumber() + 1)
                .first(customPage.isFirst())
                .last(customPage.isLast())
                .build();
    }

}
