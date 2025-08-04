package com.lineinc.erp.api.server.domain.materialmanagement.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMaterialManagement is a Querydsl query type for MaterialManagement
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMaterialManagement extends EntityPathBase<MaterialManagement> {

    private static final long serialVersionUID = 661606432L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMaterialManagement materialManagement = new QMaterialManagement("materialManagement");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final DateTimePath<java.time.OffsetDateTime> deliveryDate = createDateTime("deliveryDate", java.time.OffsetDateTime.class);

    public final ListPath<MaterialManagementDetail, QMaterialManagementDetail> details = this.<MaterialManagementDetail, QMaterialManagementDetail>createList("details", MaterialManagementDetail.class, QMaterialManagementDetail.class, PathInits.DIRECT2);

    public final ListPath<MaterialManagementFile, QMaterialManagementFile> files = this.<MaterialManagementFile, QMaterialManagementFile>createList("files", MaterialManagementFile.class, QMaterialManagementFile.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<com.lineinc.erp.api.server.domain.materialmanagement.enums.MaterialManagementInputType> inputType = createEnum("inputType", com.lineinc.erp.api.server.domain.materialmanagement.enums.MaterialManagementInputType.class);

    public final StringPath inputTypeDescription = createString("inputTypeDescription");

    public final StringPath memo = createString("memo");

    public final com.lineinc.erp.api.server.domain.site.entity.QSite site;

    public final com.lineinc.erp.api.server.domain.site.entity.QSiteProcess siteProcess;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QMaterialManagement(String variable) {
        this(MaterialManagement.class, forVariable(variable), INITS);
    }

    public QMaterialManagement(Path<? extends MaterialManagement> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMaterialManagement(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMaterialManagement(PathMetadata metadata, PathInits inits) {
        this(MaterialManagement.class, metadata, inits);
    }

    public QMaterialManagement(Class<? extends MaterialManagement> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.site = inits.isInitialized("site") ? new com.lineinc.erp.api.server.domain.site.entity.QSite(forProperty("site"), inits.get("site")) : null;
        this.siteProcess = inits.isInitialized("siteProcess") ? new com.lineinc.erp.api.server.domain.site.entity.QSiteProcess(forProperty("siteProcess"), inits.get("siteProcess")) : null;
    }

}

