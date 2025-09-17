package com.lineinc.erp.api.server.domain.common.entity;

import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.javers.core.metamodel.annotation.DiffInclude;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 모든 Entity의 공통 필드를 정의한 추상 클래스입니다.
 * - JPA Auditing 기능을 통해 생성자/수정자 자동 주입이 가능합니다.
 * - 공통 JPA 어노테이션들을 포함하여 하위 클래스에서 중복 제거
 */
@Getter
@SuperBuilder
@MappedSuperclass // JPA 엔티티 클래스가 상속할 경우, 해당 필드들을 자식 테이블 컬럼으로 인식하게 함
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class) // JPA Auditing 기능 활성화: 생성자/수정자, 생성일/수정일 자동 기록
public abstract class BaseEntity {

    /**
     * 기본키 (Primary Key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 생성 일시 자동 저장 필드
     * - 엔티티가 생성될 때 현재 시간으로 자동 설정
     */
    @CreationTimestamp
    @Column(updatable = false)
    private OffsetDateTime createdAt;

    /**
     * 수정 일시 자동 저장 필드
     * - 엔티티가 수정될 때 현재 시간으로 자동 업데이트
     */
    @Column
    @UpdateTimestamp
    private OffsetDateTime updatedAt;

    /**
     * 생성자 정보 자동 저장 필드
     * - 생성자(작성자) ID 또는 이름 등을 자동 저장
     */
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    /**
     * 수정자 정보 자동 저장 필드
     * - 마지막으로 수정한 사용자 ID 또는 이름 등을 자동 저장
     */
    @Column
    @LastModifiedBy
    private String updatedBy;

    /**
     * 삭제 여부를 나타내는 플래그 필드 (Soft Delete용)
     */
    @DiffInclude
    @Builder.Default
    @Column(nullable = false)
    private boolean deleted = false;

    /**
     * 삭제 일시 저장 필드 (Soft Delete용)
     */
    @Column
    private OffsetDateTime deletedAt;

    /**
     * 소프트 삭제 처리 메서드
     */
    public void markAsDeleted() {
        this.deleted = true;
        this.deletedAt = OffsetDateTime.now();
    }
}