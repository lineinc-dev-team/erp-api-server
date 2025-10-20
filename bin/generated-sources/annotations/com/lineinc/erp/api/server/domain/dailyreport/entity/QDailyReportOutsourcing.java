package com.lineinc.erp.api.server.domain.dailyreport.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDailyReportOutsourcing is a Querydsl query type for DailyReportOutsourcing
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDailyReportOutsourcing extends EntityPathBase<DailyReportOutsourcing> {

    private static final long serialVersionUID = -1511072716L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDailyReportOutsourcing dailyReportOutsourcing = new QDailyReportOutsourcing("dailyReportOutsourcing");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final StringPath category = createString("category");

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final QDailyReport dailyReport;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath memo = createString("memo");

    public final com.lineinc.erp.api.server.domain.outsourcingcompany.entity.QOutsourcingCompany outsourcingCompany;

    public final com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.QOutsourcingCompanyContractWorker outsourcingCompanyContractWorker;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final StringPath workContent = createString("workContent");

    public final NumberPath<Double> workQuantity = createNumber("workQuantity", Double.class);

    public QDailyReportOutsourcing(String variable) {
        this(DailyReportOutsourcing.class, forVariable(variable), INITS);
    }

    public QDailyReportOutsourcing(Path<? extends DailyReportOutsourcing> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDailyReportOutsourcing(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDailyReportOutsourcing(PathMetadata metadata, PathInits inits) {
        this(DailyReportOutsourcing.class, metadata, inits);
    }

    public QDailyReportOutsourcing(Class<? extends DailyReportOutsourcing> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.dailyReport = inits.isInitialized("dailyReport") ? new QDailyReport(forProperty("dailyReport"), inits.get("dailyReport")) : null;
        this.outsourcingCompany = inits.isInitialized("outsourcingCompany") ? new com.lineinc.erp.api.server.domain.outsourcingcompany.entity.QOutsourcingCompany(forProperty("outsourcingCompany")) : null;
        this.outsourcingCompanyContractWorker = inits.isInitialized("outsourcingCompanyContractWorker") ? new com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.QOutsourcingCompanyContractWorker(forProperty("outsourcingCompanyContractWorker"), inits.get("outsourcingCompanyContractWorker")) : null;
    }

}

