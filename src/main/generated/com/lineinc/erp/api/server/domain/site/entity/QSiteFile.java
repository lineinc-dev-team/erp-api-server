package com.lineinc.erp.api.server.domain.site.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSiteFile is a Querydsl query type for SiteFile
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSiteFile extends EntityPathBase<SiteFile> {

    private static final long serialVersionUID = -2038509802L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSiteFile siteFile = new QSiteFile("siteFile");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final StringPath fileUrl = createString("fileUrl");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath memo = createString("memo");

    public final StringPath name = createString("name");

    public final StringPath originalFileName = createString("originalFileName");

    public final QSiteContract siteContract;

    public final EnumPath<com.lineinc.erp.api.server.domain.site.enums.SiteFileType> type = createEnum("type", com.lineinc.erp.api.server.domain.site.enums.SiteFileType.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QSiteFile(String variable) {
        this(SiteFile.class, forVariable(variable), INITS);
    }

    public QSiteFile(Path<? extends SiteFile> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSiteFile(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSiteFile(PathMetadata metadata, PathInits inits) {
        this(SiteFile.class, metadata, inits);
    }

    public QSiteFile(Class<? extends SiteFile> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.siteContract = inits.isInitialized("siteContract") ? new QSiteContract(forProperty("siteContract"), inits.get("siteContract")) : null;
    }

}

