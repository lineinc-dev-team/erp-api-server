package com.lineinc.erp.api.server.domain.labor.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLabor is a Querydsl query type for Labor
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLabor extends EntityPathBase<Labor> {

    private static final long serialVersionUID = 1312361362L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLabor labor = new QLabor("labor");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final StringPath accountHolder = createString("accountHolder");

    public final StringPath accountNumber = createString("accountNumber");

    public final StringPath address = createString("address");

    public final StringPath bankName = createString("bankName");

    public final ListPath<LaborChangeHistory, QLaborChangeHistory> changeHistories = this.<LaborChangeHistory, QLaborChangeHistory>createList("changeHistories", LaborChangeHistory.class, QLaborChangeHistory.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final NumberPath<Long> dailyWage = createNumber("dailyWage", Long.class);

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final StringPath detailAddress = createString("detailAddress");

    public final ListPath<LaborFile, QLaborFile> files = this.<LaborFile, QLaborFile>createList("files", LaborFile.class, QLaborFile.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.OffsetDateTime> firstWorkDate = createDateTime("firstWorkDate", java.time.OffsetDateTime.class);

    public final com.lineinc.erp.api.server.domain.organization.entity.QGrade grade;

    public final DateTimePath<java.time.OffsetDateTime> hireDate = createDateTime("hireDate", java.time.OffsetDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isHeadOffice = createBoolean("isHeadOffice");

    public final BooleanPath isSeverancePayEligible = createBoolean("isSeverancePayEligible");

    public final BooleanPath isTemporary = createBoolean("isTemporary");

    public final StringPath mainWork = createString("mainWork");

    public final StringPath memo = createString("memo");

    public final StringPath name = createString("name");

    public final com.lineinc.erp.api.server.domain.outsourcingcompany.entity.QOutsourcingCompany outsourcingCompany;

    public final com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.QOutsourcingCompanyContract outsourcingCompanyContract;

    public final StringPath phoneNumber = createString("phoneNumber");

    public final NumberPath<Long> previousDailyWage = createNumber("previousDailyWage", Long.class);

    public final StringPath residentNumber = createString("residentNumber");

    public final DateTimePath<java.time.OffsetDateTime> resignationDate = createDateTime("resignationDate", java.time.OffsetDateTime.class);

    public final NumberPath<Integer> tenureMonths = createNumber("tenureMonths", Integer.class);

    public final EnumPath<com.lineinc.erp.api.server.domain.labor.enums.LaborType> type = createEnum("type", com.lineinc.erp.api.server.domain.labor.enums.LaborType.class);

    public final StringPath typeDescription = createString("typeDescription");

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final EnumPath<com.lineinc.erp.api.server.domain.labor.enums.LaborWorkType> workType = createEnum("workType", com.lineinc.erp.api.server.domain.labor.enums.LaborWorkType.class);

    public final StringPath workTypeDescription = createString("workTypeDescription");

    public QLabor(String variable) {
        this(Labor.class, forVariable(variable), INITS);
    }

    public QLabor(Path<? extends Labor> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLabor(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLabor(PathMetadata metadata, PathInits inits) {
        this(Labor.class, metadata, inits);
    }

    public QLabor(Class<? extends Labor> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.grade = inits.isInitialized("grade") ? new com.lineinc.erp.api.server.domain.organization.entity.QGrade(forProperty("grade")) : null;
        this.outsourcingCompany = inits.isInitialized("outsourcingCompany") ? new com.lineinc.erp.api.server.domain.outsourcingcompany.entity.QOutsourcingCompany(forProperty("outsourcingCompany")) : null;
        this.outsourcingCompanyContract = inits.isInitialized("outsourcingCompanyContract") ? new com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.QOutsourcingCompanyContract(forProperty("outsourcingCompanyContract"), inits.get("outsourcingCompanyContract")) : null;
    }

}

