package com.lineinc.erp.api.server.domain.dailyreport.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDailyReportOutsourcingConstruction is a Querydsl query type for DailyReportOutsourcingConstruction
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDailyReportOutsourcingConstruction extends EntityPathBase<DailyReportOutsourcingConstruction> {

    private static final long serialVersionUID = -805989595L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDailyReportOutsourcingConstruction dailyReportOutsourcingConstruction = new QDailyReportOutsourcingConstruction("dailyReportOutsourcingConstruction");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final StringPath contractFileUrl = createString("contractFileUrl");

    public final StringPath contractOriginalFileName = createString("contractOriginalFileName");

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

    public final com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.QOutsourcingCompanyContractConstruction outsourcingCompanyContractConstruction;

    public final QDailyReportOutsourcingConstructionGroup outsourcingConstructionGroup;

    public final NumberPath<Integer> quantity = createNumber("quantity", Integer.class);

    public final StringPath specification = createString("specification");

    public final StringPath unit = createString("unit");

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QDailyReportOutsourcingConstruction(String variable) {
        this(DailyReportOutsourcingConstruction.class, forVariable(variable), INITS);
    }

    public QDailyReportOutsourcingConstruction(Path<? extends DailyReportOutsourcingConstruction> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDailyReportOutsourcingConstruction(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDailyReportOutsourcingConstruction(PathMetadata metadata, PathInits inits) {
        this(DailyReportOutsourcingConstruction.class, metadata, inits);
    }

    public QDailyReportOutsourcingConstruction(Class<? extends DailyReportOutsourcingConstruction> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.outsourcingCompanyContractConstruction = inits.isInitialized("outsourcingCompanyContractConstruction") ? new com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.QOutsourcingCompanyContractConstruction(forProperty("outsourcingCompanyContractConstruction"), inits.get("outsourcingCompanyContractConstruction")) : null;
        this.outsourcingConstructionGroup = inits.isInitialized("outsourcingConstructionGroup") ? new QDailyReportOutsourcingConstructionGroup(forProperty("outsourcingConstructionGroup"), inits.get("outsourcingConstructionGroup")) : null;
    }

}

