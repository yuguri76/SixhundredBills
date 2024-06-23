package com.sparta.sixhundredbills.timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Setter
@Getter
@MappedSuperclass // JPA 엔터티 클래스에서 공통 매핑 정보를 정의하기 위한 애노테이션
@EntityListeners(AuditingEntityListener.class) // 엔터티의 변경 사항을 감지하고 Auditing 기능을 활성화하는 JPA 애노테이션
public abstract class TimeStamp {


    @CreatedDate // 엔터티가 생성될 때 자동으로 생성일을 매핑하는 Spring Data JPA 애노테이션
    @Column(updatable = false) // 데이터베이스 열의 매핑 정보를 지정하는 JPA 애노테이션
    private LocalDateTime createdAt; // 생성 일시를 나타내는 필드

    @LastModifiedDate // 엔터티가 마지막으로 수정될 때 자동으로 수정일을 매핑하는 Spring Data JPA 애노테이션
    @Column // 데이터베이스 열의 매핑 정보를 지정하는 JPA 애노테이션
    private LocalDateTime modifiedAt; // 수정 일시를 나타내는 필드

    // 수정 일시를 설정하기 위한 메서드
    public void setUpdatedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
}


