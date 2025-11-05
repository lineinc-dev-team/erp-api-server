package com.lineinc.erp.api.server.domain.steelmanagementv2.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSteelManagementChangeHistoryV2 is a Querydsl query type for SteelManagementChangeHistoryV2
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSteelManagementChangeHistoryV2 extends EntityPathBase<SteelManagementChangeHistoryV2> {

    private static final long serialVersionUID = 385999626L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSteelManagementChangeHistoryV2 steelManagementChangeHistoryV2 = new QSteelManagementChangeHistoryV2("steelManagementChangeHistoryV2");

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

    public final QSteelManagementV2 steelManagementV2;

    public final EnumPath<com.lineinc.erp.api.server.domain.steelmanagementv2.enums.SteelManagementChangeHistoryV2Type> type = createEnum("type", com.lineinc.erp.api.server.domain.steelmanagementv2.enums.SteelManagementChangeHistoryV2Type.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final com.lineinc.erp.api.server.domain.user.entity.QUser user;

    public QSteelManagementChangeHistoryV2(String variable) {
        this(SteelManagementChangeHistoryV2.class, forVariable(variable), INITS);
    }

    public QSteelManagementChangeHistoryV2(Path<? extends SteelManagementChangeHistoryV2> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSteelManagementChangeHistoryV2(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSteelManagementChangeHistoryV2(PathMetadata metadata, PathInits inits) {
        this(SteelManagementChangeHistoryV2.class, metadata, inits);
    }

    public QSteelManagementChangeHistoryV2(Class<? extends SteelManagementChangeHistoryV2> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.steelManagementV2 = inits.isInitialized("steelManagementV2") ? new QSteelManagementV2(forProperty("steelManagementV2"), inits.get("steelManagementV2")) : null;
        this.user = inits.isInitialized("user") ? new com.lineinc.erp.api.server.domain.user.entity.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

