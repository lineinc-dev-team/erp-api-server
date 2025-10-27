package com.lineinc.erp.api.server.domain.dailyreport.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDailyReportDirectContract is a Querydsl query type for DailyReportDirectContract
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDailyReportDirectContract extends EntityPathBase<DailyReportDirectContract> {

    private static final long serialVersionUID = 578505869L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDailyReportDirectContract dailyReportDirectContract = new QDailyReportDirectContract("dailyReportDirectContract");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final QDailyReport dailyReport;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final StringPath fileUrl = createString("fileUrl");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.lineinc.erp.api.server.domain.labor.entity.QLabor labor;

    public final StringPath memo = createString("memo");

    public final StringPath originalFileName = createString("originalFileName");

    public final com.lineinc.erp.api.server.domain.outsourcingcompany.entity.QOutsourcingCompany outsourcingCompany;

    public final StringPath position = createString("position");

    public final NumberPath<Long> unitPrice = createNumber("unitPrice", Long.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final StringPath workContent = createString("workContent");

    public final NumberPath<Double> workQuantity = createNumber("workQuantity", Double.class);

    public QDailyReportDirectContract(String variable) {
        this(DailyReportDirectContract.class, forVariable(variable), INITS);
    }

    public QDailyReportDirectContract(Path<? extends DailyReportDirectContract> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDailyReportDirectContract(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDailyReportDirectContract(PathMetadata metadata, PathInits inits) {
        this(DailyReportDirectContract.class, metadata, inits);
    }

    public QDailyReportDirectContract(Class<? extends DailyReportDirectContract> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.dailyReport = inits.isInitialized("dailyReport") ? new QDailyReport(forProperty("dailyReport"), inits.get("dailyReport")) : null;
        this.labor = inits.isInitialized("labor") ? new com.lineinc.erp.api.server.domain.labor.entity.QLabor(forProperty("labor"), inits.get("labor")) : null;
        this.outsourcingCompany = inits.isInitialized("outsourcingCompany") ? new com.lineinc.erp.api.server.domain.outsourcingcompany.entity.QOutsourcingCompany(forProperty("outsourcingCompany")) : null;
    }

}

