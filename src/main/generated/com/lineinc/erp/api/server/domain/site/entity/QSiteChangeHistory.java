package com.lineinc.erp.api.server.domain.site.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSiteChangeHistory is a Querydsl query type for SiteChangeHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSiteChangeHistory extends EntityPathBase<SiteChangeHistory> {

    private static final long serialVersionUID = -479709590L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSiteChangeHistory siteChangeHistory = new QSiteChangeHistory("siteChangeHistory");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final StringPath changeDetail = createString("changeDetail");

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

    public final QSite site;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QSiteChangeHistory(String variable) {
        this(SiteChangeHistory.class, forVariable(variable), INITS);
    }

    public QSiteChangeHistory(Path<? extends SiteChangeHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSiteChangeHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSiteChangeHistory(PathMetadata metadata, PathInits inits) {
        this(SiteChangeHistory.class, metadata, inits);
    }

    public QSiteChangeHistory(Class<? extends SiteChangeHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.site = inits.isInitialized("site") ? new QSite(forProperty("site"), inits.get("site")) : null;
    }

}

