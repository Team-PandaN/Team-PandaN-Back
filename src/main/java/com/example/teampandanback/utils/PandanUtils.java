package com.example.teampandanback.utils;

import com.example.teampandanback.exception.ApiRequestException;
import com.querydsl.core.BooleanBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.example.teampandanback.domain.note.QNote.note;

@Component
public class PandanUtils {

    // String 자료형으로 받은 날짜를 LocalDate 자료형으로 형변환
    public LocalDate changeType(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateString, formatter);
        return date;
    }


    // 검색을 위해 받은 keyword를 적절한 조건으로 parsing 하여 List 형태로 반환
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

    //QueryDSL 검색 시 where 절에 조건을 추가하는 BooleanBuilder 메소드입니다
    public BooleanBuilder searchByTitleBooleanBuilder(List<String> keywordList) {
        BooleanBuilder builder = new BooleanBuilder();
        for(String keyword : keywordList){
            builder.and(note.title.toLowerCase().contains(keyword));
        }
        return builder;
    }

}
