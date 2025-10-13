package com.lineinc.erp.api.server.domain.dailyreport.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDailyReportEvidenceFile is a Querydsl query type for DailyReportEvidenceFile
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDailyReportEvidenceFile extends EntityPathBase<DailyReportEvidenceFile> {

    private static final long serialVersionUID = -1777160859L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDailyReportEvidenceFile dailyReportEvidenceFile = new QDailyReportEvidenceFile("dailyReportEvidenceFile");

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

    public final EnumPath<com.lineinc.erp.api.server.domain.dailyreport.enums.DailyReportEvidenceFileType> fileType = createEnum("fileType", com.lineinc.erp.api.server.domain.dailyreport.enums.DailyReportEvidenceFileType.class);

    public final StringPath fileUrl = createString("fileUrl");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath memo = createString("memo");

    public final StringPath name = createString("name");

    public final StringPath originalFileName = createString("originalFileName");

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QDailyReportEvidenceFile(String variable) {
        this(DailyReportEvidenceFile.class, forVariable(variable), INITS);
    }

    public QDailyReportEvidenceFile(Path<? extends DailyReportEvidenceFile> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDailyReportEvidenceFile(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDailyReportEvidenceFile(PathMetadata metadata, PathInits inits) {
        this(DailyReportEvidenceFile.class, metadata, inits);
    }

    public QDailyReportEvidenceFile(Class<? extends DailyReportEvidenceFile> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.dailyReport = inits.isInitialized("dailyReport") ? new QDailyReport(forProperty("dailyReport"), inits.get("dailyReport")) : null;
    }

}

