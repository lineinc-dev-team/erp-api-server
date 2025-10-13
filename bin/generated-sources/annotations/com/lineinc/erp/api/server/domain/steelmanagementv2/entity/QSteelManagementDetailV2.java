package com.lineinc.erp.api.server.domain.steelmanagementv2.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSteelManagementDetailV2 is a Querydsl query type for SteelManagementDetailV2
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSteelManagementDetailV2 extends EntityPathBase<SteelManagementDetailV2> {

    private static final long serialVersionUID = 1143506755L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSteelManagementDetailV2 steelManagementDetailV2 = new QSteelManagementDetailV2("steelManagementDetailV2");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    public final EnumPath<com.lineinc.erp.api.server.domain.steelmanagementv2.enums.SteelManagementDetailV2Category> category = createEnum("category", com.lineinc.erp.api.server.domain.steelmanagementv2.enums.SteelManagementDetailV2Category.class);

    public final NumberPath<Integer> count = createNumber("count", Integer.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final StringPath fileUrl = createString("fileUrl");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath memo = createString("memo");

    public final StringPath name = createString("name");

    public final StringPath originalFileName = createString("originalFileName");

    public final com.lineinc.erp.api.server.domain.outsourcingcompany.entity.QOutsourcingCompany outsourcingCompany;

    public final StringPath specification = createString("specification");

    public final QSteelManagementV2 steelManagementV2;

    public final NumberPath<Double> totalWeight = createNumber("totalWeight", Double.class);

    public final EnumPath<com.lineinc.erp.api.server.domain.steelmanagementv2.enums.SteelManagementDetailV2Type> type = createEnum("type", com.lineinc.erp.api.server.domain.steelmanagementv2.enums.SteelManagementDetailV2Type.class);

    public final NumberPath<Long> unitPrice = createNumber("unitPrice", Long.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final NumberPath<Double> weight = createNumber("weight", Double.class);

    public QSteelManagementDetailV2(String variable) {
        this(SteelManagementDetailV2.class, forVariable(variable), INITS);
    }

    public QSteelManagementDetailV2(Path<? extends SteelManagementDetailV2> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSteelManagementDetailV2(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSteelManagementDetailV2(PathMetadata metadata, PathInits inits) {
        this(SteelManagementDetailV2.class, metadata, inits);
    }

    public QSteelManagementDetailV2(Class<? extends SteelManagementDetailV2> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.outsourcingCompany = inits.isInitialized("outsourcingCompany") ? new com.lineinc.erp.api.server.domain.outsourcingcompany.entity.QOutsourcingCompany(forProperty("outsourcingCompany")) : null;
        this.steelManagementV2 = inits.isInitialized("steelManagementV2") ? new QSteelManagementV2(forProperty("steelManagementV2"), inits.get("steelManagementV2")) : null;
    }

}

