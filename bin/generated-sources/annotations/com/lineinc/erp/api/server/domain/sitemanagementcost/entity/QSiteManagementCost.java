package com.lineinc.erp.api.server.domain.sitemanagementcost.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSiteManagementCost is a Querydsl query type for SiteManagementCost
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSiteManagementCost extends EntityPathBase<SiteManagementCost> {

    private static final long serialVersionUID = 2024558554L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSiteManagementCost siteManagementCost = new QSiteManagementCost("siteManagementCost");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final NumberPath<Long> contractGuaranteeFee = createNumber("contractGuaranteeFee", Long.class);

    public final NumberPath<Long> contractGuaranteeFeeDeduction = createNumber("contractGuaranteeFeeDeduction", Long.class);

    public final StringPath contractGuaranteeFeeMemo = createString("contractGuaranteeFeeMemo");

    public final NumberPath<Long> contractGuaranteeFeeSupplyPrice = createNumber("contractGuaranteeFeeSupplyPrice", Long.class);

    public final NumberPath<Long> contractGuaranteeFeeVat = createNumber("contractGuaranteeFeeVat", Long.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> employeeSalary = createNumber("employeeSalary", Long.class);

    public final NumberPath<Long> employeeSalaryDeduction = createNumber("employeeSalaryDeduction", Long.class);

    public final StringPath employeeSalaryMemo = createString("employeeSalaryMemo");

    public final NumberPath<Long> employeeSalarySupplyPrice = createNumber("employeeSalarySupplyPrice", Long.class);

    public final NumberPath<Long> employeeSalaryVat = createNumber("employeeSalaryVat", Long.class);

    public final NumberPath<Long> equipmentGuaranteeFee = createNumber("equipmentGuaranteeFee", Long.class);

    public final NumberPath<Long> equipmentGuaranteeFeeDeduction = createNumber("equipmentGuaranteeFeeDeduction", Long.class);

    public final StringPath equipmentGuaranteeFeeMemo = createString("equipmentGuaranteeFeeMemo");

    public final NumberPath<Long> equipmentGuaranteeFeeSupplyPrice = createNumber("equipmentGuaranteeFeeSupplyPrice", Long.class);

    public final NumberPath<Long> equipmentGuaranteeFeeVat = createNumber("equipmentGuaranteeFeeVat", Long.class);

    public final NumberPath<Long> headquartersManagementCost = createNumber("headquartersManagementCost", Long.class);

    public final StringPath headquartersManagementCostMemo = createString("headquartersManagementCostMemo");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> majorInsuranceDaily = createNumber("majorInsuranceDaily", Long.class);

    public final NumberPath<Long> majorInsuranceDailyDeduction = createNumber("majorInsuranceDailyDeduction", Long.class);

    public final StringPath majorInsuranceDailyMemo = createString("majorInsuranceDailyMemo");

    public final NumberPath<Long> majorInsuranceDailySupplyPrice = createNumber("majorInsuranceDailySupplyPrice", Long.class);

    public final NumberPath<Long> majorInsuranceDailyVat = createNumber("majorInsuranceDailyVat", Long.class);

    public final NumberPath<Long> majorInsuranceRegular = createNumber("majorInsuranceRegular", Long.class);

    public final NumberPath<Long> majorInsuranceRegularDeduction = createNumber("majorInsuranceRegularDeduction", Long.class);

    public final StringPath majorInsuranceRegularMemo = createString("majorInsuranceRegularMemo");

    public final NumberPath<Long> majorInsuranceRegularSupplyPrice = createNumber("majorInsuranceRegularSupplyPrice", Long.class);

    public final NumberPath<Long> majorInsuranceRegularVat = createNumber("majorInsuranceRegularVat", Long.class);

    public final NumberPath<Long> nationalTaxPayment = createNumber("nationalTaxPayment", Long.class);

    public final NumberPath<Long> nationalTaxPaymentDeduction = createNumber("nationalTaxPaymentDeduction", Long.class);

    public final StringPath nationalTaxPaymentMemo = createString("nationalTaxPaymentMemo");

    public final NumberPath<Long> nationalTaxPaymentSupplyPrice = createNumber("nationalTaxPaymentSupplyPrice", Long.class);

    public final NumberPath<Long> nationalTaxPaymentVat = createNumber("nationalTaxPaymentVat", Long.class);

    public final NumberPath<Long> regularRetirementPension = createNumber("regularRetirementPension", Long.class);

    public final NumberPath<Long> regularRetirementPensionDeduction = createNumber("regularRetirementPensionDeduction", Long.class);

    public final StringPath regularRetirementPensionMemo = createString("regularRetirementPensionMemo");

    public final NumberPath<Long> regularRetirementPensionSupplyPrice = createNumber("regularRetirementPensionSupplyPrice", Long.class);

    public final NumberPath<Long> regularRetirementPensionVat = createNumber("regularRetirementPensionVat", Long.class);

    public final NumberPath<Long> retirementDeduction = createNumber("retirementDeduction", Long.class);

    public final NumberPath<Long> retirementDeductionDeduction = createNumber("retirementDeductionDeduction", Long.class);

    public final StringPath retirementDeductionMemo = createString("retirementDeductionMemo");

    public final NumberPath<Long> retirementDeductionSupplyPrice = createNumber("retirementDeductionSupplyPrice", Long.class);

    public final NumberPath<Long> retirementDeductionVat = createNumber("retirementDeductionVat", Long.class);

    public final com.lineinc.erp.api.server.domain.site.entity.QSite site;

    public final com.lineinc.erp.api.server.domain.site.entity.QSiteProcess siteProcess;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final StringPath yearMonth = createString("yearMonth");

    public QSiteManagementCost(String variable) {
        this(SiteManagementCost.class, forVariable(variable), INITS);
    }

    public QSiteManagementCost(Path<? extends SiteManagementCost> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSiteManagementCost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSiteManagementCost(PathMetadata metadata, PathInits inits) {
        this(SiteManagementCost.class, metadata, inits);
    }

    public QSiteManagementCost(Class<? extends SiteManagementCost> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.site = inits.isInitialized("site") ? new com.lineinc.erp.api.server.domain.site.entity.QSite(forProperty("site"), inits.get("site")) : null;
        this.siteProcess = inits.isInitialized("siteProcess") ? new com.lineinc.erp.api.server.domain.site.entity.QSiteProcess(forProperty("siteProcess"), inits.get("siteProcess")) : null;
    }

}

