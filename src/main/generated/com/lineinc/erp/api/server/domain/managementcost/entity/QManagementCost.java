package com.lineinc.erp.api.server.domain.managementcost.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QManagementCost is a Querydsl query type for ManagementCost
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QManagementCost extends EntityPathBase<ManagementCost> {

    private static final long serialVersionUID = -1961443860L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QManagementCost managementCost = new QManagementCost("managementCost");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final StringPath accountHolder = createString("accountHolder");

    public final StringPath accountNumber = createString("accountNumber");

    public final StringPath bankName = createString("bankName");

    public final StringPath businessNumber = createString("businessNumber");

    public final StringPath ceoName = createString("ceoName");

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath itemDescription = createString("itemDescription");

    public final EnumPath<com.lineinc.erp.api.server.domain.managementcost.enums.ItemType> itemType = createEnum("itemType", com.lineinc.erp.api.server.domain.managementcost.enums.ItemType.class);

    public final StringPath memo = createString("memo");

    public final DateTimePath<java.time.OffsetDateTime> paymentDate = createDateTime("paymentDate", java.time.OffsetDateTime.class);

    public final com.lineinc.erp.api.server.domain.site.entity.QSite site;

    public final com.lineinc.erp.api.server.domain.site.entity.QSiteProcess siteProcess;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QManagementCost(String variable) {
        this(ManagementCost.class, forVariable(variable), INITS);
    }

    public QManagementCost(Path<? extends ManagementCost> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QManagementCost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QManagementCost(PathMetadata metadata, PathInits inits) {
        this(ManagementCost.class, metadata, inits);
    }

    public QManagementCost(Class<? extends ManagementCost> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.site = inits.isInitialized("site") ? new com.lineinc.erp.api.server.domain.site.entity.QSite(forProperty("site"), inits.get("site")) : null;
        this.siteProcess = inits.isInitialized("siteProcess") ? new com.lineinc.erp.api.server.domain.site.entity.QSiteProcess(forProperty("siteProcess"), inits.get("siteProcess")) : null;
    }

}

