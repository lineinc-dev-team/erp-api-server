package com.lineinc.erp.api.server.domain.dailyreport.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDailyReportMainProcess is a Querydsl query type for DailyReportMainProcess
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDailyReportMainProcess extends EntityPathBase<DailyReportMainProcess> {

    private static final long serialVersionUID = -1830293692L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDailyReportMainProcess dailyReportMainProcess = new QDailyReportMainProcess("dailyReportMainProcess");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final NumberPath<Long> contractAmount = createNumber("contractAmount", Long.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final NumberPath<Long> cumulativeAmount = createNumber("cumulativeAmount", Long.class);

    public final QDailyReport dailyReport;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> previousDayAmount = createNumber("previousDayAmount", Long.class);

    public final StringPath process = createString("process");

    public final NumberPath<Double> processRate = createNumber("processRate", Double.class);

    public final NumberPath<Long> todayAmount = createNumber("todayAmount", Long.class);

    public final StringPath unit = createString("unit");

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QDailyReportMainProcess(String variable) {
        this(DailyReportMainProcess.class, forVariable(variable), INITS);
    }

    public QDailyReportMainProcess(Path<? extends DailyReportMainProcess> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDailyReportMainProcess(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDailyReportMainProcess(PathMetadata metadata, PathInits inits) {
        this(DailyReportMainProcess.class, metadata, inits);
    }

    public QDailyReportMainProcess(Class<? extends DailyReportMainProcess> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.dailyReport = inits.isInitialized("dailyReport") ? new QDailyReport(forProperty("dailyReport"), inits.get("dailyReport")) : null;
    }

}

