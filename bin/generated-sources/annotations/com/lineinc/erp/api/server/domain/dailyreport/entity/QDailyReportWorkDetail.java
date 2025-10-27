package com.lineinc.erp.api.server.domain.dailyreport.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDailyReportWorkDetail is a Querydsl query type for DailyReportWorkDetail
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDailyReportWorkDetail extends EntityPathBase<DailyReportWorkDetail> {

    private static final long serialVersionUID = 1133230132L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDailyReportWorkDetail dailyReportWorkDetail = new QDailyReportWorkDetail("dailyReportWorkDetail");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath personnelAndEquipment = createString("personnelAndEquipment");

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final QDailyReportWork work;

    public QDailyReportWorkDetail(String variable) {
        this(DailyReportWorkDetail.class, forVariable(variable), INITS);
    }

    public QDailyReportWorkDetail(Path<? extends DailyReportWorkDetail> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDailyReportWorkDetail(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDailyReportWorkDetail(PathMetadata metadata, PathInits inits) {
        this(DailyReportWorkDetail.class, metadata, inits);
    }

    public QDailyReportWorkDetail(Class<? extends DailyReportWorkDetail> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.work = inits.isInitialized("work") ? new QDailyReportWork(forProperty("work"), inits.get("work")) : null;
    }

}

