package com.lineinc.erp.api.server.domain.laborpayroll.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLaborPayroll is a Querydsl query type for LaborPayroll
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLaborPayroll extends EntityPathBase<LaborPayroll> {

    private static final long serialVersionUID = 979965110L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLaborPayroll laborPayroll = new QLaborPayroll("laborPayroll");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final NumberPath<Integer> dailyWage = createNumber("dailyWage", Integer.class);

    public final NumberPath<Double> day01Hours = createNumber("day01Hours", Double.class);

    public final NumberPath<Double> day02Hours = createNumber("day02Hours", Double.class);

    public final NumberPath<Double> day03Hours = createNumber("day03Hours", Double.class);

    public final NumberPath<Double> day04Hours = createNumber("day04Hours", Double.class);

    public final NumberPath<Double> day05Hours = createNumber("day05Hours", Double.class);

    public final NumberPath<Double> day06Hours = createNumber("day06Hours", Double.class);

    public final NumberPath<Double> day07Hours = createNumber("day07Hours", Double.class);

    public final NumberPath<Double> day08Hours = createNumber("day08Hours", Double.class);

    public final NumberPath<Double> day09Hours = createNumber("day09Hours", Double.class);

    public final NumberPath<Double> day10Hours = createNumber("day10Hours", Double.class);

    public final NumberPath<Double> day11Hours = createNumber("day11Hours", Double.class);

    public final NumberPath<Double> day12Hours = createNumber("day12Hours", Double.class);

    public final NumberPath<Double> day13Hours = createNumber("day13Hours", Double.class);

    public final NumberPath<Double> day14Hours = createNumber("day14Hours", Double.class);

    public final NumberPath<Double> day15Hours = createNumber("day15Hours", Double.class);

    public final NumberPath<Double> day16Hours = createNumber("day16Hours", Double.class);

    public final NumberPath<Double> day17Hours = createNumber("day17Hours", Double.class);

    public final NumberPath<Double> day18Hours = createNumber("day18Hours", Double.class);

    public final NumberPath<Double> day19Hours = createNumber("day19Hours", Double.class);

    public final NumberPath<Double> day20Hours = createNumber("day20Hours", Double.class);

    public final NumberPath<Double> day21Hours = createNumber("day21Hours", Double.class);

    public final NumberPath<Double> day22Hours = createNumber("day22Hours", Double.class);

    public final NumberPath<Double> day23Hours = createNumber("day23Hours", Double.class);

    public final NumberPath<Double> day24Hours = createNumber("day24Hours", Double.class);

    public final NumberPath<Double> day25Hours = createNumber("day25Hours", Double.class);

    public final NumberPath<Double> day26Hours = createNumber("day26Hours", Double.class);

    public final NumberPath<Double> day27Hours = createNumber("day27Hours", Double.class);

    public final NumberPath<Double> day28Hours = createNumber("day28Hours", Double.class);

    public final NumberPath<Double> day29Hours = createNumber("day29Hours", Double.class);

    public final NumberPath<Double> day30Hours = createNumber("day30Hours", Double.class);

    public final NumberPath<Double> day31Hours = createNumber("day31Hours", Double.class);

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<java.math.BigDecimal> employmentInsurance = createNumber("employmentInsurance", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> healthInsurance = createNumber("healthInsurance", java.math.BigDecimal.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<java.math.BigDecimal> incomeTax = createNumber("incomeTax", java.math.BigDecimal.class);

    public final com.lineinc.erp.api.server.domain.labor.entity.QLabor labor;

    public final NumberPath<java.math.BigDecimal> localTax = createNumber("localTax", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> longTermCareInsurance = createNumber("longTermCareInsurance", java.math.BigDecimal.class);

    public final StringPath memo = createString("memo");

    public final NumberPath<java.math.BigDecimal> nationalPension = createNumber("nationalPension", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> netPayment = createNumber("netPayment", java.math.BigDecimal.class);

    public final com.lineinc.erp.api.server.domain.site.entity.QSite site;

    public final com.lineinc.erp.api.server.domain.site.entity.QSiteProcess siteProcess;

    public final NumberPath<java.math.BigDecimal> totalDeductions = createNumber("totalDeductions", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> totalLaborCost = createNumber("totalLaborCost", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> totalWorkDays = createNumber("totalWorkDays", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> totalWorkHours = createNumber("totalWorkHours", java.math.BigDecimal.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final StringPath yearMonth = createString("yearMonth");

    public QLaborPayroll(String variable) {
        this(LaborPayroll.class, forVariable(variable), INITS);
    }

    public QLaborPayroll(Path<? extends LaborPayroll> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLaborPayroll(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLaborPayroll(PathMetadata metadata, PathInits inits) {
        this(LaborPayroll.class, metadata, inits);
    }

    public QLaborPayroll(Class<? extends LaborPayroll> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.labor = inits.isInitialized("labor") ? new com.lineinc.erp.api.server.domain.labor.entity.QLabor(forProperty("labor"), inits.get("labor")) : null;
        this.site = inits.isInitialized("site") ? new com.lineinc.erp.api.server.domain.site.entity.QSite(forProperty("site"), inits.get("site")) : null;
        this.siteProcess = inits.isInitialized("siteProcess") ? new com.lineinc.erp.api.server.domain.site.entity.QSiteProcess(forProperty("siteProcess"), inits.get("siteProcess")) : null;
    }

}

