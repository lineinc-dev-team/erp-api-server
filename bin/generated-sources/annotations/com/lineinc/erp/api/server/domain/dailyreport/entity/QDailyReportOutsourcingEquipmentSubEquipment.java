package com.lineinc.erp.api.server.domain.dailyreport.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDailyReportOutsourcingEquipmentSubEquipment is a Querydsl query type for DailyReportOutsourcingEquipmentSubEquipment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDailyReportOutsourcingEquipmentSubEquipment extends EntityPathBase<DailyReportOutsourcingEquipmentSubEquipment> {

    private static final long serialVersionUID = 758743048L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDailyReportOutsourcingEquipmentSubEquipment dailyReportOutsourcingEquipmentSubEquipment = new QDailyReportOutsourcingEquipmentSubEquipment("dailyReportOutsourcingEquipmentSubEquipment");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final QDailyReportOutsourcingEquipment dailyReportOutsourcingEquipment;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath memo = createString("memo");

    public final com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.QOutsourcingCompanyContractSubEquipment outsourcingCompanyContractSubEquipment;

    public final NumberPath<Long> unitPrice = createNumber("unitPrice", Long.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final StringPath workContent = createString("workContent");

    public final NumberPath<Double> workHours = createNumber("workHours", Double.class);

    public QDailyReportOutsourcingEquipmentSubEquipment(String variable) {
        this(DailyReportOutsourcingEquipmentSubEquipment.class, forVariable(variable), INITS);
    }

    public QDailyReportOutsourcingEquipmentSubEquipment(Path<? extends DailyReportOutsourcingEquipmentSubEquipment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDailyReportOutsourcingEquipmentSubEquipment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDailyReportOutsourcingEquipmentSubEquipment(PathMetadata metadata, PathInits inits) {
        this(DailyReportOutsourcingEquipmentSubEquipment.class, metadata, inits);
    }

    public QDailyReportOutsourcingEquipmentSubEquipment(Class<? extends DailyReportOutsourcingEquipmentSubEquipment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.dailyReportOutsourcingEquipment = inits.isInitialized("dailyReportOutsourcingEquipment") ? new QDailyReportOutsourcingEquipment(forProperty("dailyReportOutsourcingEquipment"), inits.get("dailyReportOutsourcingEquipment")) : null;
        this.outsourcingCompanyContractSubEquipment = inits.isInitialized("outsourcingCompanyContractSubEquipment") ? new com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.QOutsourcingCompanyContractSubEquipment(forProperty("outsourcingCompanyContractSubEquipment"), inits.get("outsourcingCompanyContractSubEquipment")) : null;
    }

}

