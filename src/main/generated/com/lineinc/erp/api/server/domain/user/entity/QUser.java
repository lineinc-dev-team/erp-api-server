package com.lineinc.erp.api.server.domain.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -990993022L;

    public static final QUser user = new QUser("user");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isActive = createBoolean("isActive");

    public final StringPath landlineNumber = createString("landlineNumber");

    public final DateTimePath<java.time.OffsetDateTime> lastLoginAt = createDateTime("lastLoginAt", java.time.OffsetDateTime.class);

    public final StringPath loginId = createString("loginId");

    public final StringPath memo = createString("memo");

    public final StringPath passwordHash = createString("passwordHash");

    public final DateTimePath<java.time.OffsetDateTime> passwordResetAt = createDateTime("passwordResetAt", java.time.OffsetDateTime.class);

    public final StringPath phoneNumber = createString("phoneNumber");

    public final SetPath<com.lineinc.erp.api.server.domain.role.entity.Role, com.lineinc.erp.api.server.domain.role.entity.QRole> roles = this.<com.lineinc.erp.api.server.domain.role.entity.Role, com.lineinc.erp.api.server.domain.role.entity.QRole>createSet("roles", com.lineinc.erp.api.server.domain.role.entity.Role.class, com.lineinc.erp.api.server.domain.role.entity.QRole.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final StringPath username = createString("username");

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

