package com.lineinc.erp.api.server.domain.batch.entity;

import java.time.Duration;
import java.time.OffsetDateTime;

import com.lineinc.erp.api.server.domain.batch.enums.BatchExecutionHistoryStatus;
import com.lineinc.erp.api.server.domain.batch.enums.BatchExecutionType;
import com.lineinc.erp.api.server.domain.batch.enums.BatchName;
import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.shared.constant.AppConstants;

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
public class BatchExecutionHistory extends BaseEntity {
    private static final String SEQUENCE_NAME = "batch_execution_history_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BatchName batchName;

    @Column(nullable = false)
    private OffsetDateTime startTime;

    private OffsetDateTime endTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BatchExecutionHistoryStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BatchExecutionType executionType;

    private Double executionTimeSeconds;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    public void markAsCompleted() {
        this.endTime = OffsetDateTime.now();
        this.status = BatchExecutionHistoryStatus.COMPLETED;
        this.executionTimeSeconds = Duration.between(this.startTime, this.endTime).toMillis() / 1000.0;
    }

    public void markAsFailed(final String errorMessage) {
        this.endTime = OffsetDateTime.now();
        this.status = BatchExecutionHistoryStatus.FAILED;
        this.errorMessage = errorMessage;
        this.executionTimeSeconds = Duration.between(this.startTime, this.endTime).toMillis() / 1000.0;
    }
}
