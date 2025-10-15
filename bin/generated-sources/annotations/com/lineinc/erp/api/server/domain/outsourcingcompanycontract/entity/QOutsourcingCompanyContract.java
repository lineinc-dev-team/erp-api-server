package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOutsourcingCompanyContract is a Querydsl query type for OutsourcingCompanyContract
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOutsourcingCompanyContract extends EntityPathBase<OutsourcingCompanyContract> {

    private static final long serialVersionUID = -975195458L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOutsourcingCompanyContract outsourcingCompanyContract = new QOutsourcingCompanyContract("outsourcingCompanyContract");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final EnumPath<com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums.OutsourcingCompanyContractCategoryType> category = createEnum("category", com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums.OutsourcingCompanyContractCategoryType.class);

    public final ListPath<OutsourcingCompanyContractChangeHistory, QOutsourcingCompanyContractChangeHistory> changeHistories = this.<OutsourcingCompanyContractChangeHistory, QOutsourcingCompanyContractChangeHistory>createList("changeHistories", OutsourcingCompanyContractChangeHistory.class, QOutsourcingCompanyContractChangeHistory.class, PathInits.DIRECT2);

    public final ListPath<OutsourcingCompanyContractConstruction, QOutsourcingCompanyContractConstruction> constructions = this.<OutsourcingCompanyContractConstruction, QOutsourcingCompanyContractConstruction>createList("constructions", OutsourcingCompanyContractConstruction.class, QOutsourcingCompanyContractConstruction.class, PathInits.DIRECT2);

    public final ListPath<OutsourcingCompanyContractContact, QOutsourcingCompanyContractContact> contacts = this.<OutsourcingCompanyContractContact, QOutsourcingCompanyContractContact>createList("contacts", OutsourcingCompanyContractContact.class, QOutsourcingCompanyContractContact.class, PathInits.DIRECT2);

    public final NumberPath<Long> contractAmount = createNumber("contractAmount", Long.class);

    public final DateTimePath<java.time.OffsetDateTime> contractEndDate = createDateTime("contractEndDate", java.time.OffsetDateTime.class);

    public final DateTimePath<java.time.OffsetDateTime> contractStartDate = createDateTime("contractStartDate", java.time.OffsetDateTime.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final StringPath defaultDeductions = createString("defaultDeductions");

    public final StringPath defaultDeductionsDescription = createString("defaultDeductionsDescription");

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final ListPath<OutsourcingCompanyContractDriver, QOutsourcingCompanyContractDriver> drivers = this.<OutsourcingCompanyContractDriver, QOutsourcingCompanyContractDriver>createList("drivers", OutsourcingCompanyContractDriver.class, QOutsourcingCompanyContractDriver.class, PathInits.DIRECT2);

    public final ListPath<OutsourcingCompanyContractEquipment, QOutsourcingCompanyContractEquipment> equipments = this.<OutsourcingCompanyContractEquipment, QOutsourcingCompanyContractEquipment>createList("equipments", OutsourcingCompanyContractEquipment.class, QOutsourcingCompanyContractEquipment.class, PathInits.DIRECT2);

    public final ListPath<OutsourcingCompanyContractFile, QOutsourcingCompanyContractFile> files = this.<OutsourcingCompanyContractFile, QOutsourcingCompanyContractFile>createList("files", OutsourcingCompanyContractFile.class, QOutsourcingCompanyContractFile.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath memo = createString("memo");

    public final com.lineinc.erp.api.server.domain.outsourcingcompany.entity.QOutsourcingCompany outsourcingCompany;

    public final com.lineinc.erp.api.server.domain.site.entity.QSite site;

    public final com.lineinc.erp.api.server.domain.site.entity.QSiteProcess siteProcess;

    public final EnumPath<com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums.OutsourcingCompanyContractStatus> status = createEnum("status", com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums.OutsourcingCompanyContractStatus.class);

    public final EnumPath<com.lineinc.erp.api.server.domain.outsourcingcompany.enums.OutsourcingCompanyTaxInvoiceConditionType> taxInvoiceCondition = createEnum("taxInvoiceCondition", com.lineinc.erp.api.server.domain.outsourcingcompany.enums.OutsourcingCompanyTaxInvoiceConditionType.class);

    public final NumberPath<Integer> taxInvoiceIssueDayOfMonth = createNumber("taxInvoiceIssueDayOfMonth", Integer.class);

    public final EnumPath<com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums.OutsourcingCompanyContractType> type = createEnum("type", com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums.OutsourcingCompanyContractType.class);

    public final StringPath typeDescription = createString("typeDescription");

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final ListPath<OutsourcingCompanyContractWorker, QOutsourcingCompanyContractWorker> workers = this.<OutsourcingCompanyContractWorker, QOutsourcingCompanyContractWorker>createList("workers", OutsourcingCompanyContractWorker.class, QOutsourcingCompanyContractWorker.class, PathInits.DIRECT2);

    public QOutsourcingCompanyContract(String variable) {
        this(OutsourcingCompanyContract.class, forVariable(variable), INITS);
    }

    public QOutsourcingCompanyContract(Path<? extends OutsourcingCompanyContract> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOutsourcingCompanyContract(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOutsourcingCompanyContract(PathMetadata metadata, PathInits inits) {
        this(OutsourcingCompanyContract.class, metadata, inits);
    }

    public QOutsourcingCompanyContract(Class<? extends OutsourcingCompanyContract> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.outsourcingCompany = inits.isInitialized("outsourcingCompany") ? new com.lineinc.erp.api.server.domain.outsourcingcompany.entity.QOutsourcingCompany(forProperty("outsourcingCompany")) : null;
        this.site = inits.isInitialized("site") ? new com.lineinc.erp.api.server.domain.site.entity.QSite(forProperty("site"), inits.get("site")) : null;
        this.siteProcess = inits.isInitialized("siteProcess") ? new com.lineinc.erp.api.server.domain.site.entity.QSiteProcess(forProperty("siteProcess"), inits.get("siteProcess")) : null;
    }

}

