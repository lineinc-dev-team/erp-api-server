package com.lineinc.erp.api.server.domain.sitemanagementcost.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSiteManagementCostChangeHistory is a Querydsl query type for SiteManagementCostChangeHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSiteManagementCostChangeHistory extends EntityPathBase<SiteManagementCostChangeHistory> {

    private static final long serialVersionUID = -210905846L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSiteManagementCostChangeHistory siteManagementCostChangeHistory = new QSiteManagementCostChangeHistory("siteManagementCostChangeHistory");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final StringPath changes = createString("changes");

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath memo = createString("memo");

    public final QSiteManagementCost siteManagementCost;

    public final EnumPath<com.lineinc.erp.api.server.domain.sitemanagementcost.enums.SiteManagementCostChangeHistoryType> type = createEnum("type", com.lineinc.erp.api.server.domain.sitemanagementcost.enums.SiteManagementCostChangeHistoryType.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final com.lineinc.erp.api.server.domain.user.entity.QUser user;

    public QSiteManagementCostChangeHistory(String variable) {
        this(SiteManagementCostChangeHistory.class, forVariable(variable), INITS);
    }

    public QSiteManagementCostChangeHistory(Path<? extends SiteManagementCostChangeHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSiteManagementCostChangeHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSiteManagementCostChangeHistory(PathMetadata metadata, PathInits inits) {
        this(SiteManagementCostChangeHistory.class, metadata, inits);
    }

    public QSiteManagementCostChangeHistory(Class<? extends SiteManagementCostChangeHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.siteManagementCost = inits.isInitialized("siteManagementCost") ? new QSiteManagementCost(forProperty("siteManagementCost"), inits.get("siteManagementCost")) : null;
        this.user = inits.isInitialized("user") ? new com.lineinc.erp.api.server.domain.user.entity.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

