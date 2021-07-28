package com.example.teampandanback.domain;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Timestamped {

    // 최초 생성 시점
    @CreatedDate
    @Column(name = "CREATED_DATE")
    private LocalDateTime createdAt;

    // 마지막 변경 시점
    @LastModifiedDate
    @Column(name = "MODIFIED_DATE")
    private LocalDateTime modifiedAt;
}