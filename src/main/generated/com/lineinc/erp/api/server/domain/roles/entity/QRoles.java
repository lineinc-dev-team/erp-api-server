package com.lineinc.erp.api.server.domain.roles.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRoles is a Querydsl query type for Roles
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRoles extends EntityPathBase<Roles> {

    private static final long serialVersionUID = -2109220686L;

    public static final QRoles roles = new QRoles("roles");

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

    public final StringPath name = createString("name");

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QRoles(String variable) {
        super(Roles.class, forVariable(variable));
    }

    public QRoles(Path<? extends Roles> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRoles(PathMetadata metadata) {
        super(Roles.class, metadata);
    }

}

