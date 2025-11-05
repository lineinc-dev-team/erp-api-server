package com.lineinc.erp.api.server.domain.batch.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBatchExecutionHistory is a Querydsl query type for BatchExecutionHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBatchExecutionHistory extends EntityPathBase<BatchExecutionHistory> {

    private static final long serialVersionUID = -875040178L;

    public static final QBatchExecutionHistory batchExecutionHistory = new QBatchExecutionHistory("batchExecutionHistory");

    public final StringPath batchName = createString("batchName");

    public final DateTimePath<java.time.OffsetDateTime> endTime = createDateTime("endTime", java.time.OffsetDateTime.class);

    public final StringPath errorMessage = createString("errorMessage");

    public final NumberPath<Double> executionTimeSeconds = createNumber("executionTimeSeconds", Double.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.OffsetDateTime> startTime = createDateTime("startTime", java.time.OffsetDateTime.class);

    public final EnumPath<com.lineinc.erp.api.server.domain.batch.enums.BatchExecutionHistoryStatus> status = createEnum("status", com.lineinc.erp.api.server.domain.batch.enums.BatchExecutionHistoryStatus.class);

    public QBatchExecutionHistory(String variable) {
        super(BatchExecutionHistory.class, forVariable(variable));
    }

    public QBatchExecutionHistory(Path<? extends BatchExecutionHistory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBatchExecutionHistory(PathMetadata metadata) {
        super(BatchExecutionHistory.class, metadata);
    }

}

