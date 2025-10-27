package com.lineinc.erp.api.server.domain.dailyreport.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDailyReportWork is a Querydsl query type for DailyReportWork
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDailyReportWork extends EntityPathBase<DailyReportWork> {

    private static final long serialVersionUID = -1288470013L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDailyReportWork dailyReportWork = new QDailyReportWork("dailyReportWork");

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

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isToday = createBoolean("isToday");

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final ListPath<DailyReportWorkDetail, QDailyReportWorkDetail> workDetails = this.<DailyReportWorkDetail, QDailyReportWorkDetail>createList("workDetails", DailyReportWorkDetail.class, QDailyReportWorkDetail.class, PathInits.DIRECT2);

    public final StringPath workName = createString("workName");

    public QDailyReportWork(String variable) {
        this(DailyReportWork.class, forVariable(variable), INITS);
    }

    public QDailyReportWork(Path<? extends DailyReportWork> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDailyReportWork(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDailyReportWork(PathMetadata metadata, PathInits inits) {
        this(DailyReportWork.class, metadata, inits);
    }

    public QDailyReportWork(Class<? extends DailyReportWork> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.dailyReport = inits.isInitialized("dailyReport") ? new QDailyReport(forProperty("dailyReport"), inits.get("dailyReport")) : null;
    }

}

