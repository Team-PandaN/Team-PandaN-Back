package com.example.teampandanback.dto.note.response;

import com.example.teampandanback.utils.CustomPageImpl;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class NoteSearchResponseDto {
    private List<OrdinaryNoteEachResponseDto> notes;
    private int totalPages;
    private long totalElements;
    private int pageNumber;
    private boolean first;
    private boolean last;

    @Builder
    public NoteSearchResponseDto(List<OrdinaryNoteEachResponseDto> notes, int totalPages, long totalElements, int pageNumber, boolean first, boolean last) {
        this.notes = notes;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.pageNumber = pageNumber;
        this.first = first;
        this.last = last;
    }

    public static NoteSearchResponseDto fromEntity(List<OrdinaryNoteEachResponseDto> noteResponseDtoList, CustomPageImpl customPage) {
        return NoteSearchResponseDto.builder()
                .notes(noteResponseDtoList)
                .totalPages(customPage.getTotalPages())
                .totalElements(customPage.getTotalElements())
                .pageNumber(customPage.getNumber())
                .first(customPage.isFirst())
                .last(customPage.isLast())
                .build();
    }
}
