package com.lineinc.erp.api.server.domain.fuelaggregation.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFuelAggregation is a Querydsl query type for FuelAggregation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFuelAggregation extends EntityPathBase<FuelAggregation> {

    private static final long serialVersionUID = -1384933710L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFuelAggregation fuelAggregation = new QFuelAggregation("fuelAggregation");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final DateTimePath<java.time.OffsetDateTime> date = createDateTime("date", java.time.OffsetDateTime.class);

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final ListPath<FuelInfo, QFuelInfo> fuelInfos = this.<FuelInfo, QFuelInfo>createList("fuelInfos", FuelInfo.class, QFuelInfo.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.lineinc.erp.api.server.domain.site.entity.QSite site;

    public final com.lineinc.erp.api.server.domain.site.entity.QSiteProcess siteProcess;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final EnumPath<com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelAggregationWeatherType> weather = createEnum("weather", com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelAggregationWeatherType.class);

    public QFuelAggregation(String variable) {
        this(FuelAggregation.class, forVariable(variable), INITS);
    }

    public QFuelAggregation(Path<? extends FuelAggregation> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFuelAggregation(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFuelAggregation(PathMetadata metadata, PathInits inits) {
        this(FuelAggregation.class, metadata, inits);
    }

    public QFuelAggregation(Class<? extends FuelAggregation> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.site = inits.isInitialized("site") ? new com.lineinc.erp.api.server.domain.site.entity.QSite(forProperty("site"), inits.get("site")) : null;
        this.siteProcess = inits.isInitialized("siteProcess") ? new com.lineinc.erp.api.server.domain.site.entity.QSiteProcess(forProperty("siteProcess"), inits.get("siteProcess")) : null;
    }

}

