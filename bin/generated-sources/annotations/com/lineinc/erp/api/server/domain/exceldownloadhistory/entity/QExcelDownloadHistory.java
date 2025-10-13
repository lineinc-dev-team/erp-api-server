package com.lineinc.erp.api.server.domain.exceldownloadhistory.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QExcelDownloadHistory is a Querydsl query type for ExcelDownloadHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QExcelDownloadHistory extends EntityPathBase<ExcelDownloadHistory> {

    private static final long serialVersionUID = 737215894L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QExcelDownloadHistory excelDownloadHistory = new QExcelDownloadHistory("excelDownloadHistory");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final EnumPath<com.lineinc.erp.api.server.domain.exceldownloadhistory.enums.ExcelDownloadType> downloadType = createEnum("downloadType", com.lineinc.erp.api.server.domain.exceldownloadhistory.enums.ExcelDownloadType.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final com.lineinc.erp.api.server.domain.user.entity.QUser user;

    public QExcelDownloadHistory(String variable) {
        this(ExcelDownloadHistory.class, forVariable(variable), INITS);
    }

    public QExcelDownloadHistory(Path<? extends ExcelDownloadHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QExcelDownloadHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QExcelDownloadHistory(PathMetadata metadata, PathInits inits) {
        this(ExcelDownloadHistory.class, metadata, inits);
    }

    public QExcelDownloadHistory(Class<? extends ExcelDownloadHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.lineinc.erp.api.server.domain.user.entity.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

