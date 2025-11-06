package com.lineinc.erp.api.server.domain.managementcost.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QManagementCostMealFeeDetailEquipment is a Querydsl query type for ManagementCostMealFeeDetailEquipment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QManagementCostMealFeeDetailEquipment extends EntityPathBase<ManagementCostMealFeeDetailEquipment> {

    private static final long serialVersionUID = -745988602L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QManagementCostMealFeeDetailEquipment managementCostMealFeeDetailEquipment = new QManagementCostMealFeeDetailEquipment("managementCostMealFeeDetailEquipment");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    public final NumberPath<Integer> breakfastCount = createNumber("breakfastCount", Integer.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Integer> dinnerCount = createNumber("dinnerCount", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> lunchCount = createNumber("lunchCount", Integer.class);

    public final QManagementCost managementCost;

    public final StringPath memo = createString("memo");

    public final com.lineinc.erp.api.server.domain.outsourcingcompany.entity.QOutsourcingCompany outsourcingCompany;

    public final com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.QOutsourcingCompanyContractDriver outsourcingCompanyContractDriver;

    public final NumberPath<Long> unitPrice = createNumber("unitPrice", Long.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QManagementCostMealFeeDetailEquipment(String variable) {
        this(ManagementCostMealFeeDetailEquipment.class, forVariable(variable), INITS);
    }

    public QManagementCostMealFeeDetailEquipment(Path<? extends ManagementCostMealFeeDetailEquipment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QManagementCostMealFeeDetailEquipment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QManagementCostMealFeeDetailEquipment(PathMetadata metadata, PathInits inits) {
        this(ManagementCostMealFeeDetailEquipment.class, metadata, inits);
    }

    public QManagementCostMealFeeDetailEquipment(Class<? extends ManagementCostMealFeeDetailEquipment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.managementCost = inits.isInitialized("managementCost") ? new QManagementCost(forProperty("managementCost"), inits.get("managementCost")) : null;
        this.outsourcingCompany = inits.isInitialized("outsourcingCompany") ? new com.lineinc.erp.api.server.domain.outsourcingcompany.entity.QOutsourcingCompany(forProperty("outsourcingCompany")) : null;
        this.outsourcingCompanyContractDriver = inits.isInitialized("outsourcingCompanyContractDriver") ? new com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.QOutsourcingCompanyContractDriver(forProperty("outsourcingCompanyContractDriver"), inits.get("outsourcingCompanyContractDriver")) : null;
    }

}

