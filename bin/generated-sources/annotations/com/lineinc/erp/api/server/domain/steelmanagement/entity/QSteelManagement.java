package com.lineinc.erp.api.server.domain.steelmanagement.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSteelManagement is a Querydsl query type for SteelManagement
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSteelManagement extends EntityPathBase<SteelManagement> {

    private static final long serialVersionUID = 341961906L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSteelManagement steelManagement = new QSteelManagement("steelManagement");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final DateTimePath<java.time.OffsetDateTime> approvalDate = createDateTime("approvalDate", java.time.OffsetDateTime.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final ListPath<SteelManagementDetail, QSteelManagementDetail> details = this.<SteelManagementDetail, QSteelManagementDetail>createList("details", SteelManagementDetail.class, QSteelManagementDetail.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.OffsetDateTime> endDate = createDateTime("endDate", java.time.OffsetDateTime.class);

    public final ListPath<SteelManagementFile, QSteelManagementFile> files = this.<SteelManagementFile, QSteelManagementFile>createList("files", SteelManagementFile.class, QSteelManagementFile.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath memo = createString("memo");

    public final DateTimePath<java.time.OffsetDateTime> orderDate = createDateTime("orderDate", java.time.OffsetDateTime.class);

    public final com.lineinc.erp.api.server.domain.outsourcingcompany.entity.QOutsourcingCompany outsourcingCompany;

    public final EnumPath<com.lineinc.erp.api.server.domain.steelmanagement.enums.SteelManagementType> previousType = createEnum("previousType", com.lineinc.erp.api.server.domain.steelmanagement.enums.SteelManagementType.class);

    public final DateTimePath<java.time.OffsetDateTime> releaseDate = createDateTime("releaseDate", java.time.OffsetDateTime.class);

    public final ListPath<SteelManagementReturnDetail, QSteelManagementReturnDetail> returnDetails = this.<SteelManagementReturnDetail, QSteelManagementReturnDetail>createList("returnDetails", SteelManagementReturnDetail.class, QSteelManagementReturnDetail.class, PathInits.DIRECT2);

    public final com.lineinc.erp.api.server.domain.site.entity.QSite site;

    public final com.lineinc.erp.api.server.domain.site.entity.QSiteProcess siteProcess;

    public final DateTimePath<java.time.OffsetDateTime> startDate = createDateTime("startDate", java.time.OffsetDateTime.class);

    public final EnumPath<com.lineinc.erp.api.server.domain.steelmanagement.enums.SteelManagementType> type = createEnum("type", com.lineinc.erp.api.server.domain.steelmanagement.enums.SteelManagementType.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final StringPath usage = createString("usage");

    public QSteelManagement(String variable) {
        this(SteelManagement.class, forVariable(variable), INITS);
    }

    public QSteelManagement(Path<? extends SteelManagement> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSteelManagement(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSteelManagement(PathMetadata metadata, PathInits inits) {
        this(SteelManagement.class, metadata, inits);
    }

    public QSteelManagement(Class<? extends SteelManagement> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.outsourcingCompany = inits.isInitialized("outsourcingCompany") ? new com.lineinc.erp.api.server.domain.outsourcingcompany.entity.QOutsourcingCompany(forProperty("outsourcingCompany")) : null;
        this.site = inits.isInitialized("site") ? new com.lineinc.erp.api.server.domain.site.entity.QSite(forProperty("site"), inits.get("site")) : null;
        this.siteProcess = inits.isInitialized("siteProcess") ? new com.lineinc.erp.api.server.domain.site.entity.QSiteProcess(forProperty("siteProcess"), inits.get("siteProcess")) : null;
    }

}

