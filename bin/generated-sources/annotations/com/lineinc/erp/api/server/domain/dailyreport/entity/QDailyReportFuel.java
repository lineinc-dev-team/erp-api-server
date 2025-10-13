package com.lineinc.erp.api.server.domain.dailyreport.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDailyReportFuel is a Querydsl query type for DailyReportFuel
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDailyReportFuel extends EntityPathBase<DailyReportFuel> {

    private static final long serialVersionUID = -1288971096L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDailyReportFuel dailyReportFuel = new QDailyReportFuel("dailyReportFuel");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final QDailyReport dailyReport;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final com.lineinc.erp.api.server.domain.fuelaggregation.entity.QFuelAggregation fuelAggregation;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath memo = createString("memo");

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QDailyReportFuel(String variable) {
        this(DailyReportFuel.class, forVariable(variable), INITS);
    }

    public QDailyReportFuel(Path<? extends DailyReportFuel> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDailyReportFuel(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDailyReportFuel(PathMetadata metadata, PathInits inits) {
        this(DailyReportFuel.class, metadata, inits);
    }

    public QDailyReportFuel(Class<? extends DailyReportFuel> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.dailyReport = inits.isInitialized("dailyReport") ? new QDailyReport(forProperty("dailyReport"), inits.get("dailyReport")) : null;
        this.fuelAggregation = inits.isInitialized("fuelAggregation") ? new com.lineinc.erp.api.server.domain.fuelaggregation.entity.QFuelAggregation(forProperty("fuelAggregation"), inits.get("fuelAggregation")) : null;
    }

}

