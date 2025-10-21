package com.lineinc.erp.api.server.domain.steelmanagementv2.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSteelManagementV2 is a Querydsl query type for SteelManagementV2
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSteelManagementV2 extends EntityPathBase<SteelManagementV2> {

    private static final long serialVersionUID = 724822930L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSteelManagementV2 steelManagementV2 = new QSteelManagementV2("steelManagementV2");

    public final com.lineinc.erp.api.server.domain.common.entity.QBaseEntity _super = new com.lineinc.erp.api.server.domain.common.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> deletedAt = _super.deletedAt;

    public final ListPath<SteelManagementDetailV2, QSteelManagementDetailV2> details = this.<SteelManagementDetailV2, QSteelManagementDetailV2>createList("details", SteelManagementDetailV2.class, QSteelManagementDetailV2.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> incomingOwnMaterialAmount = createNumber("incomingOwnMaterialAmount", Long.class);

    public final NumberPath<Double> incomingOwnMaterialTotalWeight = createNumber("incomingOwnMaterialTotalWeight", Double.class);

    public final NumberPath<Long> incomingPurchaseAmount = createNumber("incomingPurchaseAmount", Long.class);

    public final NumberPath<Double> incomingPurchaseTotalWeight = createNumber("incomingPurchaseTotalWeight", Double.class);

    public final NumberPath<Long> incomingRentalAmount = createNumber("incomingRentalAmount", Long.class);

    public final NumberPath<Double> incomingRentalTotalWeight = createNumber("incomingRentalTotalWeight", Double.class);

    public final NumberPath<Double> onSiteRemainingWeight = createNumber("onSiteRemainingWeight", Double.class);

    public final NumberPath<Double> onSiteStockTotalWeight = createNumber("onSiteStockTotalWeight", Double.class);

    public final NumberPath<Long> outgoingOwnMaterialAmount = createNumber("outgoingOwnMaterialAmount", Long.class);

    public final NumberPath<Double> outgoingOwnMaterialTotalWeight = createNumber("outgoingOwnMaterialTotalWeight", Double.class);

    public final NumberPath<Long> outgoingPurchaseAmount = createNumber("outgoingPurchaseAmount", Long.class);

    public final NumberPath<Double> outgoingPurchaseTotalWeight = createNumber("outgoingPurchaseTotalWeight", Double.class);

    public final NumberPath<Long> outgoingRentalAmount = createNumber("outgoingRentalAmount", Long.class);

    public final NumberPath<Double> outgoingRentalTotalWeight = createNumber("outgoingRentalTotalWeight", Double.class);

    public final NumberPath<Long> scrapAmount = createNumber("scrapAmount", Long.class);

    public final NumberPath<Double> scrapTotalWeight = createNumber("scrapTotalWeight", Double.class);

    public final com.lineinc.erp.api.server.domain.site.entity.QSite site;

    public final com.lineinc.erp.api.server.domain.site.entity.QSiteProcess siteProcess;

    public final NumberPath<Long> totalInvestmentAmount = createNumber("totalInvestmentAmount", Long.class);

    //inherited
    public final DateTimePath<java.time.OffsetDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QSteelManagementV2(String variable) {
        this(SteelManagementV2.class, forVariable(variable), INITS);
    }

    public QSteelManagementV2(Path<? extends SteelManagementV2> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSteelManagementV2(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSteelManagementV2(PathMetadata metadata, PathInits inits) {
        this(SteelManagementV2.class, metadata, inits);
    }

    public QSteelManagementV2(Class<? extends SteelManagementV2> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.site = inits.isInitialized("site") ? new com.lineinc.erp.api.server.domain.site.entity.QSite(forProperty("site"), inits.get("site")) : null;
        this.siteProcess = inits.isInitialized("siteProcess") ? new com.lineinc.erp.api.server.domain.site.entity.QSiteProcess(forProperty("siteProcess"), inits.get("siteProcess")) : null;
    }

}

