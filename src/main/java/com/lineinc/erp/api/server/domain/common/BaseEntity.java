package com.lineinc.erp.api.server.domain.common;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

/**
 * 모든 Entity의 공통 필드를 정의한 추상 클래스입니다.
 * - JPA Auditing 기능을 통해 생성자/수정자 자동 주입이 가능합니다.
 */
@Getter
@MappedSuperclass // JPA 엔티티 클래스가 상속할 경우, 해당 필드들을 자식 테이블 컬럼으로 인식하게 함
@EntityListeners(AuditingEntityListener.class) // JPA Auditing 기능 활성화: 생성자/수정자, 생성일/수정일 자동 기록
public abstract class BaseEntity {

    /**
     * 생성 일시 자동 저장 필드
     * - 엔티티가 생성될 때 현재 시간으로 자동 설정
     * - 변경 불가(updatable = false)
     */
    @CreationTimestamp
    @Column(updatable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt;

    /**
     * 수정 일시 자동 저장 필드
     * - 엔티티가 수정될 때 현재 시간으로 자동 업데이트
     */
    @UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime updatedAt;

    /**
     * 생성자 정보 자동 저장 필드
     * - 생성자(작성자) ID 또는 이름 등을 자동 저장
     * - 변경 불가(updatable = false)
     * - 이 기능을 사용하려면 AuditorAware 구현체 등록 필요
     */
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    /**
     * 수정자 정보 자동 저장 필드
     * - 마지막으로 수정한 사용자 ID 또는 이름 등을 자동 저장
     * - AuditorAware 구현체에 의해 설정됨
     */
    @LastModifiedBy
    private String updatedBy;

    /**
     * 삭제 여부를 나타내는 플래그 필드 (Soft Delete용)
     * - 기본값 false (삭제되지 않음)
     * - 논리 삭제 구현 시 사용
     */
    @Column(nullable = false)
    private boolean deleted = false;

    /**
     * 삭제 일시 저장 필드 (Soft Delete용)
     * - 삭제 시점 기록
     * - 삭제되지 않은 경우 null
     */
    @Column(columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime deletedAt;
}