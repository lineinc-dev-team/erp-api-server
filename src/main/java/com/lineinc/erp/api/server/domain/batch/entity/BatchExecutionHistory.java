package com.lineinc.erp.api.server.domain.batch.entity;

import java.time.OffsetDateTime;

import org.javers.core.metamodel.annotation.DiffIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 배치 실행 이력 엔티티
 * 각 배치 작업의 실행 결과를 추적합니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class BatchExecutionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "batch_execution_history_seq")
    @SequenceGenerator(name = "batch_execution_history_seq", sequenceName = "batch_execution_history_seq", allocationSize = 1)
    private Long id;

    /**
     * 배치 작업 이름
     */
    @DiffIgnore
    @Column(nullable = false)
    private String batchName;

    /**
     * 배치 실행 시작 시간
     */
    @DiffIgnore
    @Column(nullable = false)
    private OffsetDateTime startTime;

    /**
     * 배치 실행 종료 시간
     */
    @DiffIgnore
    @Column
    private OffsetDateTime endTime;

    /**
     * 배치 실행 상태
     */
    @DiffIgnore
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BatchExecutionStatus status;

    /**
     * 실행 시간 (초)
     */
    @DiffIgnore
    @Column
    private Double executionTimeSeconds;

    /**
     * 오류 메시지 (실패 시)
     */
    @DiffIgnore
    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    /**
     * 배치 실행을 완료로 마킹합니다.
     */
    public void markAsCompleted() {
        this.endTime = OffsetDateTime.now();
        this.status = BatchExecutionStatus.COMPLETED;
        this.executionTimeSeconds = java.time.Duration.between(this.startTime, this.endTime).toMillis() / 1000.0;
    }

    /**
     * 배치 실행을 실패로 마킹합니다.
     * 
     * @param errorMessage 오류 메시지
     */
    public void markAsFailed(final String errorMessage) {
        this.endTime = OffsetDateTime.now();
        this.status = BatchExecutionStatus.FAILED;
        this.errorMessage = errorMessage;
        this.executionTimeSeconds = java.time.Duration.between(this.startTime, this.endTime).toMillis() / 1000.0;
    }

    /**
     * 배치 실행 상태 열거형
     */
    public enum BatchExecutionStatus {
        RUNNING, // 실행 중
        COMPLETED, // 완료
        FAILED // 실패
    }
}
