package com.lineinc.erp.api.server.domain.site.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSite is a Querydsl query type for Site
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSite extends EntityPathBase<Site> {

    private static final long serialVersionUID = -1634782598L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSite site = new QSite("site");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final StringPath address = createString("address");

    public final StringPath city = createString("city");

    public final com.lineinc.erp.api.server.domain.client.entity.QClientCompany clientCompany;

    public final NumberPath<Long> contractAmount = createNumber("contractAmount", Long.class);

    public final ListPath<SiteContract, QSiteContract> contracts = this.<SiteContract, QSiteContract>createList("contracts", SiteContract.class, QSiteContract.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final StringPath detailAddress = createString("detailAddress");

    public final StringPath district = createString("district");

    public final DateTimePath<java.time.OffsetDateTime> endedAt = createDateTime("endedAt", java.time.OffsetDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath memo = createString("memo");

    public final StringPath name = createString("name");

    public final ListPath<SiteProcess, QSiteProcess> processes = this.<SiteProcess, QSiteProcess>createList("processes", SiteProcess.class, QSiteProcess.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.OffsetDateTime> startedAt = createDateTime("startedAt", java.time.OffsetDateTime.class);

    public final EnumPath<com.lineinc.erp.api.server.domain.site.enums.SiteType> type = createEnum("type", com.lineinc.erp.api.server.domain.site.enums.SiteType.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final com.lineinc.erp.api.server.domain.user.entity.QUser user;

    public QSite(String variable) {
        this(Site.class, forVariable(variable), INITS);
    }

    public QSite(Path<? extends Site> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSite(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSite(PathMetadata metadata, PathInits inits) {
        this(Site.class, metadata, inits);
    }

    public QSite(Class<? extends Site> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.clientCompany = inits.isInitialized("clientCompany") ? new com.lineinc.erp.api.server.domain.client.entity.QClientCompany(forProperty("clientCompany"), inits.get("clientCompany")) : null;
        this.user = inits.isInitialized("user") ? new com.lineinc.erp.api.server.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

