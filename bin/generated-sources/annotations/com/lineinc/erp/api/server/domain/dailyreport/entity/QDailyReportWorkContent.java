package com.lineinc.erp.api.server.domain.dailyreport.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDailyReportWorkContent is a Querydsl query type for DailyReportWorkContent
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDailyReportWorkContent extends EntityPathBase<DailyReportWorkContent> {

    private static final long serialVersionUID = 164204790L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDailyReportWorkContent dailyReportWorkContent = new QDailyReportWorkContent("dailyReportWorkContent");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final StringPath content = createString("content");

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

    public final StringPath personnelAndEquipment = createString("personnelAndEquipment");

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final StringPath workName = createString("workName");

    public QDailyReportWorkContent(String variable) {
        this(DailyReportWorkContent.class, forVariable(variable), INITS);
    }

    public QDailyReportWorkContent(Path<? extends DailyReportWorkContent> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDailyReportWorkContent(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDailyReportWorkContent(PathMetadata metadata, PathInits inits) {
        this(DailyReportWorkContent.class, metadata, inits);
    }

    public QDailyReportWorkContent(Class<? extends DailyReportWorkContent> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.dailyReport = inits.isInitialized("dailyReport") ? new QDailyReport(forProperty("dailyReport"), inits.get("dailyReport")) : null;
    }

}

