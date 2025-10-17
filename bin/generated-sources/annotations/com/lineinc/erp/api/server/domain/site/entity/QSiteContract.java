package com.lineinc.erp.api.server.domain.site.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSiteContract is a Querydsl query type for SiteContract
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSiteContract extends EntityPathBase<SiteContract> {

    private static final long serialVersionUID = -2014736980L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSiteContract siteContract = new QSiteContract("siteContract");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    public final NumberPath<Long> contractPerformanceGuaranteeRate = createNumber("contractPerformanceGuaranteeRate", Long.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final NumberPath<Long> defectPerformanceGuaranteeRate = createNumber("defectPerformanceGuaranteeRate", Long.class);

    public final NumberPath<Long> defectWarrantyPeriod = createNumber("defectWarrantyPeriod", Long.class);

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final ListPath<SiteFile, QSiteFile> files = this.<SiteFile, QSiteFile>createList("files", SiteFile.class, QSiteFile.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath memo = createString("memo");

    public final StringPath name = createString("name");

    public final NumberPath<Long> purchaseTax = createNumber("purchaseTax", Long.class);

    public final QSite site;

    public final NumberPath<Long> supplyPrice = createNumber("supplyPrice", Long.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final NumberPath<Long> vat = createNumber("vat", Long.class);

    public QSiteContract(String variable) {
        this(SiteContract.class, forVariable(variable), INITS);
    }

    public QSiteContract(Path<? extends SiteContract> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSiteContract(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSiteContract(PathMetadata metadata, PathInits inits) {
        this(SiteContract.class, metadata, inits);
    }

    public QSiteContract(Class<? extends SiteContract> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.site = inits.isInitialized("site") ? new QSite(forProperty("site"), inits.get("site")) : null;
    }

}

