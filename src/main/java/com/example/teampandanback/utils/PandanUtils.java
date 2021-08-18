package com.example.teampandanback.utils;

import com.example.teampandanback.domain.note.MoveStatus;
import com.example.teampandanback.domain.note.Note;
import com.example.teampandanback.dto.note.request.NoteMoveRequestDto;
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

    // 성경:
    public PageRequest dealWithPageRequestParam(int page, int size) {
        PageRequest pageRequest = PageRequest
                .of((page <= 0 ? 1 : page) - 1, (size <= 0 ? 1 : size));
        return pageRequest;
    }

    // 승연: String 자료형으로 받은 날짜를 LocalDate 자료형으로 형변환
    public LocalDate changeType(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateString, formatter);
        return date;
    }

    // 승연: 검색을 위해 받은 keyword를 적절한 조건으로 parsing 하여 List 형태로 반환
    public List<String> parseKeywordToList(String rawKeyword) {
        List<String> rawKeywordList = Arrays.asList(rawKeyword.split(" "));
        List<String> parsedKeywordList = new ArrayList<>();

        for (String each : rawKeywordList) {
            // blank가 아니어야 하고, each의 길이가 2 이상이거나, 전체 단어의 수가 2이상이면
            if (each.length() > 1 || rawKeywordList.size() > 1 && !each.equals(" ")) {
                parsedKeywordList.add(each.toLowerCase(Locale.ROOT));
            }
        }

        if (parsedKeywordList.size() == 0) {
            throw new ApiRequestException("검색 조건에 부합하지 않습니다.");
        }

        return parsedKeywordList;
    }

    // 승연: QueryDSL 검색 시 where 절에 조건을 추가하는 BooleanBuilder 메소드입니다
    public BooleanBuilder searchByTitleBooleanBuilder(List<String> keywordList) {
        BooleanBuilder builder = new BooleanBuilder();
        for (String keyword : keywordList) {
            builder.and(note.title.toLowerCase().contains(keyword));
        }
        return builder;
    }

    // 승연: 칸반형 목록에서 topPointerList 만들기 위한 메소드입니다.
    public List<Note> getTopNoteList(List<Note> rawNoteList) {
        return rawNoteList
                .stream()
                .filter(note -> note.getNextId().toString().equals("0"))
                .collect(Collectors.toList());
    }

    // 승연: <PK,Note> 해쉬맵 만들어서 PK로 노트를 바로 찾을 수 있게 하기 위한 HashMap을 만드는 메소드입니다.
    public Map<Long, Note> getRawMap(List<Note> rawNoteList) {
        return rawNoteList
                .stream()
                .collect(Collectors.toMap(Note::getNoteId, note -> note));
    }

    // 승연: topPointer와 프로젝트 노트가 담긴 HashMap으로 스텝 별 노트를 연결 리스트 로직에 맞추어 재구성합니다.
    public List<List<KanbanNoteEachResponseDto>> getResultList(List<Note> topNoteList, Map<Long, Note> rawMap) {

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

    // 승연: 수정 시 동시성 문제가 일어나기 전에 DB와 프론트 요청의 싱크를 확인합니다.
    public Boolean checkSync(Long noteId, NoteMoveRequestDto noteMoveRequestDto,
                             List<Note> rawNoteList, Map<Long, Note> rawMap) {
        // from 연결 관계 검증 조건문
        if (rawMap.get(noteId).getPreviousId().equals(noteMoveRequestDto.getFromPreNoteId())
                && rawMap.get(noteId).getNextId().equals(noteMoveRequestDto.getFromNextNoteId())) {

            // from 연결 관계가 맞고, front 요청에서 ToNextNoteId 존재한다고 하여
            if (noteMoveRequestDto.getToNextNodeId() != 0L) {

                // 실제로 DB에서도 그렇다면 true, 아니라면 false
                return rawMap.get(noteMoveRequestDto.getToNextNodeId()).getPreviousId().equals(noteMoveRequestDto.getToPreNoteId());
            }

            // from 연결 관계가 맞고, front 요청에서 ToPreNote 존재한다고 하여
            if (noteMoveRequestDto.getToPreNoteId() != 0L) {

                // 실제로 DB에서도 그렇다면 true, 아니라면 false
                return rawMap.get(noteMoveRequestDto.getToPreNoteId()).getNextId().equals(noteMoveRequestDto.getToNextNodeId());
            }

            // from 연결 관계가 맞고, To에 아무것도 없다고 하는 주장이
            if (noteMoveRequestDto.getToPreNoteId() == 0L && noteMoveRequestDto.getToNextNodeId() == 0L){

                // 해당 step의 topNote가 없는 것으로 실제 맞는 것이 확인되면 true, 아니라면 false
                Note topNote = null;

                for (Note note1 : getTopNoteList(rawNoteList)) {
                    if (note1.getStep().toString().equals(noteMoveRequestDto.getStep())) {
                        topNote = note1;
                        break;
                    }
                }

                // 위의 과정이 끝나고서도 null 이 맞다면 true
                return topNote == null;
            }

        }
        // from 연결 관계 틀렸다면 false
        else {
            return false;
        }

        // 모든 검증에서 false 없이 왔으면 true
        return true;
    }

    // 승연: 16가지 moveStatus 중 현재 상황이 어떤 것인지 정하기 위한 메소드입니다.
    public MoveStatus[] getMoveStatus(NoteMoveRequestDto noteMoveRequestDto){
        MoveStatus[] moveStatuses = new MoveStatus[2];

        // From의 status 정한다.
        if (noteMoveRequestDto.getFromPreNoteId() == 0L && noteMoveRequestDto.getFromNextNoteId() == 0L){
            moveStatuses[0] = MoveStatus.UNIQUE;
        }
        else if (noteMoveRequestDto.getFromPreNoteId() != 0L && noteMoveRequestDto.getFromNextNoteId() == 0L){
            moveStatuses[0] = MoveStatus.CURRENTTOP;
        }
        else if (noteMoveRequestDto.getFromPreNoteId() == 0L && noteMoveRequestDto.getFromNextNoteId() != 0L){
            moveStatuses[0] = MoveStatus.CURRENTBOTTOM;
        }
        else{
            moveStatuses[0] = MoveStatus.CURRENTBETWEEN;
        }

        // To의 status 정한다.
        if (noteMoveRequestDto.getToPreNoteId() == 0L && noteMoveRequestDto.getToNextNodeId() == 0L){
            moveStatuses[1] = MoveStatus.UNIQUE;
        }
        else if (noteMoveRequestDto.getToPreNoteId() != 0L && noteMoveRequestDto.getToNextNodeId() == 0L){
            moveStatuses[1] = MoveStatus.CURRENTTOP;
        }
        else if (noteMoveRequestDto.getToPreNoteId() == 0L && noteMoveRequestDto.getToNextNodeId() != 0L){
            moveStatuses[1] = MoveStatus.CURRENTBOTTOM;
        }
        else{
            moveStatuses[1] = MoveStatus.CURRENTBETWEEN;
        }

        return moveStatuses;
    }
}
