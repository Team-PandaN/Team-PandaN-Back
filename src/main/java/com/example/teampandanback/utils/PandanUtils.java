package com.example.teampandanback.utils;

import com.example.teampandanback.domain.note.Note;
import com.example.teampandanback.dto.note.response.KanbanNoteEachResponseDto;
import com.example.teampandanback.exception.ApiRequestException;
import com.querydsl.core.BooleanBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.teampandanback.domain.note.QNote.note;

@Component
public class PandanUtils {


    // 승연: String 자료형으로 받은 날짜를 LocalDate 자료형으로 형변환
    public LocalDate changeType(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateString, formatter);
        return date;
    }

    // 승연: 검색을 위해 받은 keyword를 적절한 조건으로 parsing 하여 List 형태로 반환
    public List<String> parseKeywordToList(String rawKeyword){
        List<String> rawKeywordList = Arrays.asList(rawKeyword.split(" "));
        List<String> parsedKeywordList = new ArrayList<>();

        for (String each: rawKeywordList){
            // blank가 아니어야 하고, each의 길이가 2 이상이거나, 전체 단어의 수가 2이상이면
            if(each.length() > 1 || rawKeywordList.size() > 1 && !each.equals(" ")){
                parsedKeywordList.add(each.toLowerCase(Locale.ROOT));
            }
        }

        if (parsedKeywordList.size() == 0){
            throw new ApiRequestException("검색 조건에 부합하지 않습니다.");
        }

        return parsedKeywordList;
    }

    // 승연: QueryDSL 검색 시 where 절에 조건을 추가하는 BooleanBuilder 메소드입니다
    public BooleanBuilder searchByTitleBooleanBuilder(List<String> keywordList) {
        BooleanBuilder builder = new BooleanBuilder();
        for(String keyword : keywordList){
            builder.and(note.title.toLowerCase().contains(keyword));
        }
        return builder;
    }

    // 성경:
    public PageRequest dealWithPageRequestParam(int page, int size) {
        PageRequest pageRequest = PageRequest
                .of((page <= 0 ? 1 : page)-1, (size <= 0 ? 1 : size));
        return pageRequest;
    }

    // 승연: 칸반형 목록에서 topPointerList 만들기 위한 메소드입니다. - 승연
    public List<Note> getTopNoteList(List<Note> rawNoteList){
        return rawNoteList
                .stream()
                .filter(note->note.getNextId().toString().equals("0"))
                .collect(Collectors.toList());
    }

    // 승연: <PK,Note> 해쉬맵 만들어서 PK로 노트를 바로 찾을 수 있게 하기 위한 HashMap을 만드는 메소드입니다.
    public Map<Long, Note> getRawMap(List<Note> rawNoteList){
        return rawNoteList
                .stream()
                .collect(Collectors.toMap(Note::getNoteId, note -> note));
    }

    // 승연: topPointer와 프로젝트 노트가 담긴 HashMap으로 스텝 별 노트를 연결 리스트 로직에 맞추어 재구성합니다.
    public List<List<KanbanNoteEachResponseDto>> getResultList(List<Note> topNoteList, Map<Long, Note> rawMap){

        List<List<KanbanNoteEachResponseDto>> resultList = new ArrayList<>();

        // 각 스텝 별 노트를 담을 리스트 만들기
        List<KanbanNoteEachResponseDto> StorageNoteList = new ArrayList<>();
        List<KanbanNoteEachResponseDto> TodoNoteList = new ArrayList<>();
        List<KanbanNoteEachResponseDto> ProcessingNoteList = new ArrayList<>();
        List<KanbanNoteEachResponseDto> DoneNoteList = new ArrayList<>();

        for (Note topNote : topNoteList) {
            Note pointer = topNote;
            while (pointer != null) {
                Note target = rawMap.get(pointer.getNoteId());
                switch (pointer.getStep()) {
                    case STORAGE:
                        StorageNoteList.add(KanbanNoteEachResponseDto.of(target));
                        break;
                    case TODO:
                        TodoNoteList.add(KanbanNoteEachResponseDto.of(target));
                        break;
                    case PROCESSING:
                        ProcessingNoteList.add(KanbanNoteEachResponseDto.of(target));
                        break;
                    case DONE:
                        DoneNoteList.add(KanbanNoteEachResponseDto.of(target));
                        break;
                }
                pointer = rawMap.get(target.getPreviousId());
            }
        }

        resultList.add(StorageNoteList);
        resultList.add(TodoNoteList);
        resultList.add(ProcessingNoteList);
        resultList.add(DoneNoteList);

        return resultList;
    }

}
