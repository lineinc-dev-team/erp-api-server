package com.lineinc.erp.api.server.domain.dailyreport.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDailyReportOutsourcingConstructionGroup is a Querydsl query type for DailyReportOutsourcingConstructionGroup
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDailyReportOutsourcingConstructionGroup extends EntityPathBase<DailyReportOutsourcingConstructionGroup> {

    private static final long serialVersionUID = -53501350L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDailyReportOutsourcingConstructionGroup dailyReportOutsourcingConstructionGroup = new QDailyReportOutsourcingConstructionGroup("dailyReportOutsourcingConstructionGroup");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final ListPath<DailyReportOutsourcingConstruction, QDailyReportOutsourcingConstruction> constructions = this.<DailyReportOutsourcingConstruction, QDailyReportOutsourcingConstruction>createList("constructions", DailyReportOutsourcingConstruction.class, QDailyReportOutsourcingConstruction.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final QDailyReportOutsourcingCompany dailyReportOutsourcingCompany;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.QOutsourcingCompanyContractConstructionGroup outsourcingCompanyContractConstructionGroup;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QDailyReportOutsourcingConstructionGroup(String variable) {
        this(DailyReportOutsourcingConstructionGroup.class, forVariable(variable), INITS);
    }

    public QDailyReportOutsourcingConstructionGroup(Path<? extends DailyReportOutsourcingConstructionGroup> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDailyReportOutsourcingConstructionGroup(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDailyReportOutsourcingConstructionGroup(PathMetadata metadata, PathInits inits) {
        this(DailyReportOutsourcingConstructionGroup.class, metadata, inits);
    }

    public QDailyReportOutsourcingConstructionGroup(Class<? extends DailyReportOutsourcingConstructionGroup> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.dailyReportOutsourcingCompany = inits.isInitialized("dailyReportOutsourcingCompany") ? new QDailyReportOutsourcingCompany(forProperty("dailyReportOutsourcingCompany"), inits.get("dailyReportOutsourcingCompany")) : null;
        this.outsourcingCompanyContractConstructionGroup = inits.isInitialized("outsourcingCompanyContractConstructionGroup") ? new com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.QOutsourcingCompanyContractConstructionGroup(forProperty("outsourcingCompanyContractConstructionGroup"), inits.get("outsourcingCompanyContractConstructionGroup")) : null;
    }

}

