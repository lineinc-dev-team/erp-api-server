package com.lineinc.erp.api.server.domain.managementcost.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QManagementCost is a Querydsl query type for ManagementCost
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QManagementCost extends EntityPathBase<ManagementCost> {

    private static final long serialVersionUID = -1961443860L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QManagementCost managementCost = new QManagementCost("managementCost");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final com.lineinc.erp.api.server.domain.outsourcingcompany.entity.QOutsourcingCompany deductionCompany;

    public final com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.QOutsourcingCompanyContract deductionCompanyContract;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final ListPath<ManagementCostDetail, QManagementCostDetail> details = this.<ManagementCostDetail, QManagementCostDetail>createList("details", ManagementCostDetail.class, QManagementCostDetail.class, PathInits.DIRECT2);

    public final ListPath<ManagementCostFile, QManagementCostFile> files = this.<ManagementCostFile, QManagementCostFile>createList("files", ManagementCostFile.class, QManagementCostFile.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<com.lineinc.erp.api.server.domain.managementcost.enums.ManagementCostItemType> itemType = createEnum("itemType", com.lineinc.erp.api.server.domain.managementcost.enums.ManagementCostItemType.class);

    public final StringPath itemTypeDescription = createString("itemTypeDescription");

    public final ListPath<ManagementCostKeyMoneyDetail, QManagementCostKeyMoneyDetail> keyMoneyDetails = this.<ManagementCostKeyMoneyDetail, QManagementCostKeyMoneyDetail>createList("keyMoneyDetails", ManagementCostKeyMoneyDetail.class, QManagementCostKeyMoneyDetail.class, PathInits.DIRECT2);

    public final ListPath<ManagementCostMealFeeDetailDirectContract, QManagementCostMealFeeDetailDirectContract> mealFeeDetailDirectContracts = this.<ManagementCostMealFeeDetailDirectContract, QManagementCostMealFeeDetailDirectContract>createList("mealFeeDetailDirectContracts", ManagementCostMealFeeDetailDirectContract.class, QManagementCostMealFeeDetailDirectContract.class, PathInits.DIRECT2);

    public final ListPath<ManagementCostMealFeeDetailEquipment, QManagementCostMealFeeDetailEquipment> mealFeeDetailEquipments = this.<ManagementCostMealFeeDetailEquipment, QManagementCostMealFeeDetailEquipment>createList("mealFeeDetailEquipments", ManagementCostMealFeeDetailEquipment.class, QManagementCostMealFeeDetailEquipment.class, PathInits.DIRECT2);

    public final ListPath<ManagementCostMealFeeDetailOutsourcingContract, QManagementCostMealFeeDetailOutsourcingContract> mealFeeDetailOutsourcingContracts = this.<ManagementCostMealFeeDetailOutsourcingContract, QManagementCostMealFeeDetailOutsourcingContract>createList("mealFeeDetailOutsourcingContracts", ManagementCostMealFeeDetailOutsourcingContract.class, QManagementCostMealFeeDetailOutsourcingContract.class, PathInits.DIRECT2);

    public final ListPath<ManagementCostMealFeeDetailOutsourcing, QManagementCostMealFeeDetailOutsourcing> mealFeeDetailOutsourcings = this.<ManagementCostMealFeeDetailOutsourcing, QManagementCostMealFeeDetailOutsourcing>createList("mealFeeDetailOutsourcings", ManagementCostMealFeeDetailOutsourcing.class, QManagementCostMealFeeDetailOutsourcing.class, PathInits.DIRECT2);

    public final ListPath<ManagementCostMealFeeDetail, QManagementCostMealFeeDetail> mealFeeDetails = this.<ManagementCostMealFeeDetail, QManagementCostMealFeeDetail>createList("mealFeeDetails", ManagementCostMealFeeDetail.class, QManagementCostMealFeeDetail.class, PathInits.DIRECT2);

    public final StringPath memo = createString("memo");

    public final com.lineinc.erp.api.server.domain.outsourcingcompany.entity.QOutsourcingCompany outsourcingCompany;

    public final DateTimePath<java.time.OffsetDateTime> paymentDate = createDateTime("paymentDate", java.time.OffsetDateTime.class);

    public final com.lineinc.erp.api.server.domain.site.entity.QSite site;

    public final com.lineinc.erp.api.server.domain.site.entity.QSiteProcess siteProcess;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QManagementCost(String variable) {
        this(ManagementCost.class, forVariable(variable), INITS);
    }

    public QManagementCost(Path<? extends ManagementCost> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QManagementCost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QManagementCost(PathMetadata metadata, PathInits inits) {
        this(ManagementCost.class, metadata, inits);
    }

    public QManagementCost(Class<? extends ManagementCost> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.deductionCompany = inits.isInitialized("deductionCompany") ? new com.lineinc.erp.api.server.domain.outsourcingcompany.entity.QOutsourcingCompany(forProperty("deductionCompany")) : null;
        this.deductionCompanyContract = inits.isInitialized("deductionCompanyContract") ? new com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.QOutsourcingCompanyContract(forProperty("deductionCompanyContract"), inits.get("deductionCompanyContract")) : null;
        this.outsourcingCompany = inits.isInitialized("outsourcingCompany") ? new com.lineinc.erp.api.server.domain.outsourcingcompany.entity.QOutsourcingCompany(forProperty("outsourcingCompany")) : null;
        this.site = inits.isInitialized("site") ? new com.lineinc.erp.api.server.domain.site.entity.QSite(forProperty("site"), inits.get("site")) : null;
        this.siteProcess = inits.isInitialized("siteProcess") ? new com.lineinc.erp.api.server.domain.site.entity.QSiteProcess(forProperty("siteProcess"), inits.get("siteProcess")) : null;
    }

}

