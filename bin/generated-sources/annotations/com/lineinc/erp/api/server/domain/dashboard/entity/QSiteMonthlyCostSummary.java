package com.lineinc.erp.api.server.domain.dashboard.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSiteMonthlyCostSummary is a Querydsl query type for SiteMonthlyCostSummary
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSiteMonthlyCostSummary extends EntityPathBase<SiteMonthlyCostSummary> {

    private static final long serialVersionUID = 351104053L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSiteMonthlyCostSummary siteMonthlyCostSummary = new QSiteMonthlyCostSummary("siteMonthlyCostSummary");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> equipmentCost = createNumber("equipmentCost", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> laborCost = createNumber("laborCost", Long.class);

    public final NumberPath<Long> managementCost = createNumber("managementCost", Long.class);

    public final NumberPath<Long> materialCost = createNumber("materialCost", Long.class);

    public final NumberPath<Long> outsourcingCost = createNumber("outsourcingCost", Long.class);

    public final com.lineinc.erp.api.server.domain.site.entity.QSite site;

    public final com.lineinc.erp.api.server.domain.site.entity.QSiteProcess siteProcess;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final StringPath yearMonth = createString("yearMonth");

    public QSiteMonthlyCostSummary(String variable) {
        this(SiteMonthlyCostSummary.class, forVariable(variable), INITS);
    }

    public QSiteMonthlyCostSummary(Path<? extends SiteMonthlyCostSummary> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSiteMonthlyCostSummary(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSiteMonthlyCostSummary(PathMetadata metadata, PathInits inits) {
        this(SiteMonthlyCostSummary.class, metadata, inits);
    }

    public QSiteMonthlyCostSummary(Class<? extends SiteMonthlyCostSummary> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.site = inits.isInitialized("site") ? new com.lineinc.erp.api.server.domain.site.entity.QSite(forProperty("site"), inits.get("site")) : null;
        this.siteProcess = inits.isInitialized("siteProcess") ? new com.lineinc.erp.api.server.domain.site.entity.QSiteProcess(forProperty("siteProcess"), inits.get("siteProcess")) : null;
    }

}

