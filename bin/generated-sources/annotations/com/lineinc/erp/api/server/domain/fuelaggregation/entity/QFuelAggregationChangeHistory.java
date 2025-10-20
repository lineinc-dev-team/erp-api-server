package com.lineinc.erp.api.server.domain.fuelaggregation.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFuelAggregationChangeHistory is a Querydsl query type for FuelAggregationChangeHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFuelAggregationChangeHistory extends EntityPathBase<FuelAggregationChangeHistory> {

    private static final long serialVersionUID = 843738418L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFuelAggregationChangeHistory fuelAggregationChangeHistory = new QFuelAggregationChangeHistory("fuelAggregationChangeHistory");

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

    public final QFuelAggregation fuelAggregation;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath memo = createString("memo");

    public final EnumPath<com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelAggregationChangeType> type = createEnum("type", com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelAggregationChangeType.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QFuelAggregationChangeHistory(String variable) {
        this(FuelAggregationChangeHistory.class, forVariable(variable), INITS);
    }

    public QFuelAggregationChangeHistory(Path<? extends FuelAggregationChangeHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFuelAggregationChangeHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFuelAggregationChangeHistory(PathMetadata metadata, PathInits inits) {
        this(FuelAggregationChangeHistory.class, metadata, inits);
    }

    public QFuelAggregationChangeHistory(Class<? extends FuelAggregationChangeHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.fuelAggregation = inits.isInitialized("fuelAggregation") ? new QFuelAggregation(forProperty("fuelAggregation"), inits.get("fuelAggregation")) : null;
    }

}

