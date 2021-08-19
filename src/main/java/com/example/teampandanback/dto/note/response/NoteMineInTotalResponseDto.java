package com.example.teampandanback.dto.note.response;

import com.example.teampandanback.utils.CustomPageImpl;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class NoteMineInTotalResponseDto {
    List<NoteEachMineInTotalResponseDto> myNoteList;
    private int totalPages;
    private long totalElements;
    private int pageNumber;
    private boolean first;
    private boolean last;

    @Builder
    public NoteMineInTotalResponseDto(List<NoteEachMineInTotalResponseDto> myNoteList, int totalPages, long totalElements, int pageNumber, boolean first, boolean last) {
        this.myNoteList = myNoteList;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.pageNumber = pageNumber;
        this.first = first;
        this.last = last;
    }

    public static NoteMineInTotalResponseDto fromEntity(List<NoteEachMineInTotalResponseDto> myNoteList, CustomPageImpl customPage) {
        return NoteMineInTotalResponseDto.builder()
                .myNoteList(myNoteList)
                .totalPages(customPage.getTotalPages())
                .totalElements(customPage.getTotalElements())
                .pageNumber(customPage.getNumber() + 1)
                .first(customPage.isFirst())
                .last(customPage.isLast())
                .build();
    }

}
