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

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final EnumPath<com.lineinc.erp.api.server.domain.batch.enums.BatchName> batchName = createEnum("batchName", com.lineinc.erp.api.server.domain.batch.enums.BatchName.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final DateTimePath<java.time.OffsetDateTime> endTime = createDateTime("endTime", java.time.OffsetDateTime.class);

    public final StringPath errorMessage = createString("errorMessage");

    public final NumberPath<Double> executionTimeSeconds = createNumber("executionTimeSeconds", Double.class);

    public final EnumPath<com.lineinc.erp.api.server.domain.batch.enums.BatchExecutionType> executionType = createEnum("executionType", com.lineinc.erp.api.server.domain.batch.enums.BatchExecutionType.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.OffsetDateTime> startTime = createDateTime("startTime", java.time.OffsetDateTime.class);

    public final EnumPath<com.lineinc.erp.api.server.domain.batch.enums.BatchExecutionHistoryStatus> status = createEnum("status", com.lineinc.erp.api.server.domain.batch.enums.BatchExecutionHistoryStatus.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

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

