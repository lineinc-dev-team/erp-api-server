package com.lineinc.erp.api.server.domain.site.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSiteProcess is a Querydsl query type for SiteProcess
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSiteProcess extends EntityPathBase<SiteProcess> {

    private static final long serialVersionUID = 2137630357L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSiteProcess siteProcess = new QSiteProcess("siteProcess");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath memo = createString("memo");

    public final StringPath name = createString("name");

    public final StringPath officePhone = createString("officePhone");

    public final QSite site;

    public final EnumPath<com.lineinc.erp.api.server.domain.site.enums.SiteProcessStatus> status = createEnum("status", com.lineinc.erp.api.server.domain.site.enums.SiteProcessStatus.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QSiteProcess(String variable) {
        this(SiteProcess.class, forVariable(variable), INITS);
    }

    public QSiteProcess(Path<? extends SiteProcess> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSiteProcess(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSiteProcess(PathMetadata metadata, PathInits inits) {
        this(SiteProcess.class, metadata, inits);
    }

    public QSiteProcess(Class<? extends SiteProcess> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.site = inits.isInitialized("site") ? new QSite(forProperty("site"), inits.get("site")) : null;
    }

}

