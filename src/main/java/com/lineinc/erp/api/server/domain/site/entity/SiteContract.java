package com.lineinc.erp.api.server.domain.site.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.request.SiteContractUpdateRequest;
import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
@Table(indexes = {
        @Index(columnList = "name"),
        @Index(columnList = "amount"),
        @Index(columnList = "created_at")
})
public class SiteContract extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "site_contract_seq")
    @SequenceGenerator(name = "site_contract_seq", sequenceName = "site_contract_seq", allocationSize = 1)
    private Long id;

    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site; // 현장

    @Column(nullable = false)
    @DiffInclude
    private String name; // 계약명

    @Column
    @DiffInclude
    private Long amount; // 계약금액

    @Column
    @DiffInclude
    private Long supplyPrice; // 공급가

    @Column
    @DiffInclude
    private Long vat; // 부가세

    @Column
    @DiffInclude
    private Long purchaseTax; // 매입세

    @Column
    @DiffInclude
    private Long contractPerformanceGuaranteeRate; // 계약이행 보증률

    @Column
    @DiffInclude
    private Long defectPerformanceGuaranteeRate; // 하자이행 보증률

    @Column
    @DiffInclude
    private Long defectWarrantyPeriod; // 하자보증기간

    @Column(columnDefinition = "TEXT")
    @DiffInclude
    private String memo; // 비고

    @Setter
    @DiffInclude
    @Builder.Default
    @OneToMany(mappedBy = "siteContract", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SiteFile> files = new ArrayList<>();

    public void updateFrom(final SiteContractUpdateRequest request) {
        this.name = request.name();
        this.amount = request.amount();
        this.supplyPrice = request.supplyPrice();
        this.vat = request.vat();
        this.purchaseTax = request.purchaseTax();
        this.contractPerformanceGuaranteeRate = request.contractPerformanceGuaranteeRate();
        this.defectPerformanceGuaranteeRate = request.defectPerformanceGuaranteeRate();
        this.defectWarrantyPeriod = request.defectWarrantyPeriod();
        this.memo = request.memo();

        if (request.files() != null) {
            EntitySyncUtils.syncList(
                    this.files,
                    request.files(),
                    (fileDto) -> SiteFile.builder()
                            .siteContract(this)
                            .fileUrl(fileDto.fileUrl())
                            .originalFileName(fileDto.originalFileName())
                            .memo(fileDto.memo())
                            .type(fileDto.type())
                            .build());
        }
    }
}