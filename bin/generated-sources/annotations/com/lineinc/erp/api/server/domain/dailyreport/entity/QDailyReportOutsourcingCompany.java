package com.lineinc.erp.api.server.domain.dailyreport.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDailyReportOutsourcingCompany is a Querydsl query type for DailyReportOutsourcingCompany
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDailyReportOutsourcingCompany extends EntityPathBase<DailyReportOutsourcingCompany> {

    private static final long serialVersionUID = -1434544727L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDailyReportOutsourcingCompany dailyReportOutsourcingCompany = new QDailyReportOutsourcingCompany("dailyReportOutsourcingCompany");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final ListPath<DailyReportOutsourcingConstructionGroup, QDailyReportOutsourcingConstructionGroup> constructionGroups = this.<DailyReportOutsourcingConstructionGroup, QDailyReportOutsourcingConstructionGroup>createList("constructionGroups", DailyReportOutsourcingConstructionGroup.class, QDailyReportOutsourcingConstructionGroup.class, PathInits.DIRECT2);

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

    public final com.lineinc.erp.api.server.domain.outsourcingcompany.entity.QOutsourcingCompany outsourcingCompany;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QDailyReportOutsourcingCompany(String variable) {
        this(DailyReportOutsourcingCompany.class, forVariable(variable), INITS);
    }

    public QDailyReportOutsourcingCompany(Path<? extends DailyReportOutsourcingCompany> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDailyReportOutsourcingCompany(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDailyReportOutsourcingCompany(PathMetadata metadata, PathInits inits) {
        this(DailyReportOutsourcingCompany.class, metadata, inits);
    }

    public QDailyReportOutsourcingCompany(Class<? extends DailyReportOutsourcingCompany> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.dailyReport = inits.isInitialized("dailyReport") ? new QDailyReport(forProperty("dailyReport"), inits.get("dailyReport")) : null;
        this.outsourcingCompany = inits.isInitialized("outsourcingCompany") ? new com.lineinc.erp.api.server.domain.outsourcingcompany.entity.QOutsourcingCompany(forProperty("outsourcingCompany")) : null;
    }

}

