package com.lineinc.erp.api.server.domain.dailyreport.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDailyReportInputStatus is a Querydsl query type for DailyReportInputStatus
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDailyReportInputStatus extends EntityPathBase<DailyReportInputStatus> {

    private static final long serialVersionUID = -942209750L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDailyReportInputStatus dailyReportInputStatus = new QDailyReportInputStatus("dailyReportInputStatus");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final StringPath category = createString("category");

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final NumberPath<Long> cumulativeCount = createNumber("cumulativeCount", Long.class);

    public final QDailyReport dailyReport;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> previousDayCount = createNumber("previousDayCount", Long.class);

    public final NumberPath<Long> todayCount = createNumber("todayCount", Long.class);

    public final EnumPath<com.lineinc.erp.api.server.domain.dailyreport.enums.DailyReportInputStatusType> type = createEnum("type", com.lineinc.erp.api.server.domain.dailyreport.enums.DailyReportInputStatusType.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QDailyReportInputStatus(String variable) {
        this(DailyReportInputStatus.class, forVariable(variable), INITS);
    }

    public QDailyReportInputStatus(Path<? extends DailyReportInputStatus> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDailyReportInputStatus(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDailyReportInputStatus(PathMetadata metadata, PathInits inits) {
        this(DailyReportInputStatus.class, metadata, inits);
    }

    public QDailyReportInputStatus(Class<? extends DailyReportInputStatus> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.dailyReport = inits.isInitialized("dailyReport") ? new QDailyReport(forProperty("dailyReport"), inits.get("dailyReport")) : null;
    }

}

