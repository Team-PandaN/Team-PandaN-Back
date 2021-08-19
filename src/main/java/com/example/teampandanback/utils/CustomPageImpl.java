package com.example.teampandanback.utils;

import com.fasterxml.jackson.annotation.JsonGetter;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomPageImpl<T> extends PageImpl<T> {

    public CustomPageImpl(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    @JsonGetter(value = "paging")
    public Map getPaging() {
        Map<String, Object> paging = new HashMap<>();
        paging.put("totalPages", super.getTotalPages());
        paging.put("totalElements", super.getTotalElements());
        paging.put("pageSize", super.getSize());
        paging.put("pageNumber", super.getNumber() + 1);
        paging.put("isFirst", super.isFirst());
        paging.put("isLast", super.isLast());
        paging.put("sort", super.getSort());
        paging.put("isEmpty", super.isEmpty());
        return paging;
    }
}
