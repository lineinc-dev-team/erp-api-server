package com.lineinc.erp.api.server.domain.permission.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPermission is a Querydsl query type for Permission
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPermission extends EntityPathBase<Permission> {

    private static final long serialVersionUID = 579134474L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPermission permission = new QPermission("permission");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final EnumPath<com.lineinc.erp.api.server.domain.permission.enums.PermissionAction> action = createEnum("action", com.lineinc.erp.api.server.domain.permission.enums.PermissionAction.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.lineinc.erp.api.server.domain.menu.entity.QMenu menu;

    public final NumberPath<Integer> order = createNumber("order", Integer.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QPermission(String variable) {
        this(Permission.class, forVariable(variable), INITS);
    }

    public QPermission(Path<? extends Permission> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPermission(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPermission(PathMetadata metadata, PathInits inits) {
        this(Permission.class, metadata, inits);
    }

    public QPermission(Class<? extends Permission> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.menu = inits.isInitialized("menu") ? new com.lineinc.erp.api.server.domain.menu.entity.QMenu(forProperty("menu")) : null;
    }

}

