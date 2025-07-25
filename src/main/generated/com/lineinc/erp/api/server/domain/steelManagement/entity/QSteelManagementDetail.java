package com.lineinc.erp.api.server.domain.steelManagement.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSteelManagementDetail is a Querydsl query type for SteelManagementDetail
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSteelManagementDetail extends EntityPathBase<SteelManagementDetail> {

    private static final long serialVersionUID = -402244733L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSteelManagementDetail steelManagementDetail = new QSteelManagementDetail("steelManagementDetail");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final NumberPath<Integer> count = createNumber("count", Integer.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Double> length = createNumber("length", Double.class);

    public final StringPath memo = createString("memo");

    public final StringPath name = createString("name");

    public final NumberPath<Integer> quantity = createNumber("quantity", Integer.class);

    public final StringPath standard = createString("standard");

    public final QSteelManagement steelManagement;

    public final NumberPath<Integer> supplyPrice = createNumber("supplyPrice", Integer.class);

    public final NumberPath<Double> totalLength = createNumber("totalLength", Double.class);

    public final StringPath unit = createString("unit");

    public final NumberPath<Integer> unitPrice = createNumber("unitPrice", Integer.class);

    public final NumberPath<Double> unitWeight = createNumber("unitWeight", Double.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QSteelManagementDetail(String variable) {
        this(SteelManagementDetail.class, forVariable(variable), INITS);
    }

    public QSteelManagementDetail(Path<? extends SteelManagementDetail> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSteelManagementDetail(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSteelManagementDetail(PathMetadata metadata, PathInits inits) {
        this(SteelManagementDetail.class, metadata, inits);
    }

    public QSteelManagementDetail(Class<? extends SteelManagementDetail> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.steelManagement = inits.isInitialized("steelManagement") ? new QSteelManagement(forProperty("steelManagement"), inits.get("steelManagement")) : null;
    }

}

