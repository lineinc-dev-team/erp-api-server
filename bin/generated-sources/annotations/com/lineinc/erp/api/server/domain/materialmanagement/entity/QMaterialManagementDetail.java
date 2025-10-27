package com.lineinc.erp.api.server.domain.materialmanagement.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMaterialManagementDetail is a Querydsl query type for MaterialManagementDetail
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMaterialManagementDetail extends EntityPathBase<MaterialManagementDetail> {

    private static final long serialVersionUID = -1589003119L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMaterialManagementDetail materialManagementDetail = new QMaterialManagementDetail("materialManagementDetail");

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

    public final QMaterialManagement materialManagement;

    public final StringPath memo = createString("memo");

    public final StringPath name = createString("name");

    public final NumberPath<Integer> quantity = createNumber("quantity", Integer.class);

    public final StringPath standard = createString("standard");

    public final NumberPath<Integer> supplyPrice = createNumber("supplyPrice", Integer.class);

    public final NumberPath<Integer> total = createNumber("total", Integer.class);

    public final NumberPath<Integer> unitPrice = createNumber("unitPrice", Integer.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final StringPath usage = createString("usage");

    public final NumberPath<Integer> vat = createNumber("vat", Integer.class);

    public QMaterialManagementDetail(String variable) {
        this(MaterialManagementDetail.class, forVariable(variable), INITS);
    }

    public QMaterialManagementDetail(Path<? extends MaterialManagementDetail> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMaterialManagementDetail(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMaterialManagementDetail(PathMetadata metadata, PathInits inits) {
        this(MaterialManagementDetail.class, metadata, inits);
    }

    public QMaterialManagementDetail(Class<? extends MaterialManagementDetail> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.materialManagement = inits.isInitialized("materialManagement") ? new QMaterialManagement(forProperty("materialManagement"), inits.get("materialManagement")) : null;
    }

}

