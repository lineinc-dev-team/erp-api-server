package com.lineinc.erp.api.server.domain.role.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRole is a Querydsl query type for Role
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRole extends EntityPathBase<Role> {

    private static final long serialVersionUID = -919394728L;

    public static final QRole role = new QRole("role");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

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

    public final StringPath name = createString("name");

    public final SetPath<com.lineinc.erp.api.server.domain.permission.entity.Permission, com.lineinc.erp.api.server.domain.permission.entity.QPermission> permissions = this.<com.lineinc.erp.api.server.domain.permission.entity.Permission, com.lineinc.erp.api.server.domain.permission.entity.QPermission>createSet("permissions", com.lineinc.erp.api.server.domain.permission.entity.Permission.class, com.lineinc.erp.api.server.domain.permission.entity.QPermission.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final SetPath<com.lineinc.erp.api.server.domain.user.entity.User, com.lineinc.erp.api.server.domain.user.entity.QUser> users = this.<com.lineinc.erp.api.server.domain.user.entity.User, com.lineinc.erp.api.server.domain.user.entity.QUser>createSet("users", com.lineinc.erp.api.server.domain.user.entity.User.class, com.lineinc.erp.api.server.domain.user.entity.QUser.class, PathInits.DIRECT2);

    public QRole(String variable) {
        super(Role.class, forVariable(variable));
    }

    public QRole(Path<? extends Role> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRole(PathMetadata metadata) {
        super(Role.class, metadata);
    }

}

