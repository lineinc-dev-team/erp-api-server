package com.lineinc.erp.api.server.domain.dailyreport.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDailyReportMaterialStatus is a Querydsl query type for DailyReportMaterialStatus
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDailyReportMaterialStatus extends EntityPathBase<DailyReportMaterialStatus> {

    private static final long serialVersionUID = -8020885L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDailyReportMaterialStatus dailyReportMaterialStatus = new QDailyReportMaterialStatus("dailyReportMaterialStatus");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

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

    public final StringPath materialName = createString("materialName");

    public final NumberPath<Long> plannedAmount = createNumber("plannedAmount", Long.class);

    public final NumberPath<Long> previousDayAmount = createNumber("previousDayAmount", Long.class);

    public final NumberPath<Long> remainingAmount = createNumber("remainingAmount", Long.class);

    public final NumberPath<Long> todayAmount = createNumber("todayAmount", Long.class);

    public final EnumPath<com.lineinc.erp.api.server.domain.dailyreport.enums.DailyReportMaterialStatusType> type = createEnum("type", com.lineinc.erp.api.server.domain.dailyreport.enums.DailyReportMaterialStatusType.class);

    public final StringPath unit = createString("unit");

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QDailyReportMaterialStatus(String variable) {
        this(DailyReportMaterialStatus.class, forVariable(variable), INITS);
    }

    public QDailyReportMaterialStatus(Path<? extends DailyReportMaterialStatus> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDailyReportMaterialStatus(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDailyReportMaterialStatus(PathMetadata metadata, PathInits inits) {
        this(DailyReportMaterialStatus.class, metadata, inits);
    }

    public QDailyReportMaterialStatus(Class<? extends DailyReportMaterialStatus> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.dailyReport = inits.isInitialized("dailyReport") ? new QDailyReport(forProperty("dailyReport"), inits.get("dailyReport")) : null;
    }

}

