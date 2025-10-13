package com.lineinc.erp.api.server.domain.materialmanagement.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMaterialManagementChangeHistory is a Querydsl query type for MaterialManagementChangeHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMaterialManagementChangeHistory extends EntityPathBase<MaterialManagementChangeHistory> {

    private static final long serialVersionUID = 1546647172L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMaterialManagementChangeHistory materialManagementChangeHistory = new QMaterialManagementChangeHistory("materialManagementChangeHistory");

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

    public final QMaterialManagement materialManagement;

    public final StringPath memo = createString("memo");

    public final EnumPath<com.lineinc.erp.api.server.domain.materialmanagement.enums.MaterialManagementChangeHistoryType> type = createEnum("type", com.lineinc.erp.api.server.domain.materialmanagement.enums.MaterialManagementChangeHistoryType.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final com.lineinc.erp.api.server.domain.user.entity.QUser user;

    public QMaterialManagementChangeHistory(String variable) {
        this(MaterialManagementChangeHistory.class, forVariable(variable), INITS);
    }

    public QMaterialManagementChangeHistory(Path<? extends MaterialManagementChangeHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMaterialManagementChangeHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMaterialManagementChangeHistory(PathMetadata metadata, PathInits inits) {
        this(MaterialManagementChangeHistory.class, metadata, inits);
    }

    public QMaterialManagementChangeHistory(Class<? extends MaterialManagementChangeHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.materialManagement = inits.isInitialized("materialManagement") ? new QMaterialManagement(forProperty("materialManagement"), inits.get("materialManagement")) : null;
        this.user = inits.isInitialized("user") ? new com.lineinc.erp.api.server.domain.user.entity.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

