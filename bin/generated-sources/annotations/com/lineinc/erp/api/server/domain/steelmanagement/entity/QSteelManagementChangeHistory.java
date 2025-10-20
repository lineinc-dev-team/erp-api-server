package com.lineinc.erp.api.server.domain.steelmanagement.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSteelManagementChangeHistory is a Querydsl query type for SteelManagementChangeHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSteelManagementChangeHistory extends EntityPathBase<SteelManagementChangeHistory> {

    private static final long serialVersionUID = 2144851762L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSteelManagementChangeHistory steelManagementChangeHistory = new QSteelManagementChangeHistory("steelManagementChangeHistory");

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

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath memo = createString("memo");

    public final QSteelManagement steelManagement;

    public final EnumPath<com.lineinc.erp.api.server.domain.steelmanagement.enums.SteelManagementChangeHistoryType> type = createEnum("type", com.lineinc.erp.api.server.domain.steelmanagement.enums.SteelManagementChangeHistoryType.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QSteelManagementChangeHistory(String variable) {
        this(SteelManagementChangeHistory.class, forVariable(variable), INITS);
    }

    public QSteelManagementChangeHistory(Path<? extends SteelManagementChangeHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSteelManagementChangeHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSteelManagementChangeHistory(PathMetadata metadata, PathInits inits) {
        this(SteelManagementChangeHistory.class, metadata, inits);
    }

    public QSteelManagementChangeHistory(Class<? extends SteelManagementChangeHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.steelManagement = inits.isInitialized("steelManagement") ? new QSteelManagement(forProperty("steelManagement"), inits.get("steelManagement")) : null;
    }

}

