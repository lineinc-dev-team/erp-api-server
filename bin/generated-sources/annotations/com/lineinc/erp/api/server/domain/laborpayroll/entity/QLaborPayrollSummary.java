package com.lineinc.erp.api.server.domain.laborpayroll.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLaborPayrollSummary is a Querydsl query type for LaborPayrollSummary
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLaborPayrollSummary extends EntityPathBase<LaborPayrollSummary> {

    private static final long serialVersionUID = 1401786896L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLaborPayrollSummary laborPayrollSummary = new QLaborPayrollSummary("laborPayrollSummary");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Integer> directContractCount = createNumber("directContractCount", Integer.class);

    public final NumberPath<Integer> etcCount = createNumber("etcCount", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath memo = createString("memo");

    public final NumberPath<Integer> outsourcingCount = createNumber("outsourcingCount", Integer.class);

    public final NumberPath<Integer> regularEmployeeCount = createNumber("regularEmployeeCount", Integer.class);

    public final com.lineinc.erp.api.server.domain.site.entity.QSite site;

    public final com.lineinc.erp.api.server.domain.site.entity.QSiteProcess siteProcess;

    public final NumberPath<java.math.BigDecimal> totalDeductions = createNumber("totalDeductions", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> totalLaborCost = createNumber("totalLaborCost", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> totalNetPayment = createNumber("totalNetPayment", java.math.BigDecimal.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final StringPath yearMonth = createString("yearMonth");

    public QLaborPayrollSummary(String variable) {
        this(LaborPayrollSummary.class, forVariable(variable), INITS);
    }

    public QLaborPayrollSummary(Path<? extends LaborPayrollSummary> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLaborPayrollSummary(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLaborPayrollSummary(PathMetadata metadata, PathInits inits) {
        this(LaborPayrollSummary.class, metadata, inits);
    }

    public QLaborPayrollSummary(Class<? extends LaborPayrollSummary> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.site = inits.isInitialized("site") ? new com.lineinc.erp.api.server.domain.site.entity.QSite(forProperty("site"), inits.get("site")) : null;
        this.siteProcess = inits.isInitialized("siteProcess") ? new com.lineinc.erp.api.server.domain.site.entity.QSiteProcess(forProperty("siteProcess"), inits.get("siteProcess")) : null;
    }

}

