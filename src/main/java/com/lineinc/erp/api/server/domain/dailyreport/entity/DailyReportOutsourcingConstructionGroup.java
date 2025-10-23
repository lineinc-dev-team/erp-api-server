package com.lineinc.erp.api.server.domain.dailyreport.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLRestriction;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractConstruction;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractConstructionGroup;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.service.v1.OutsourcingCompanyContractConstructionService;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportOutsourcingConstructionUpdateRequest.ConstructionGroupUpdateInfo;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportOutsourcingConstructionUpdateRequest.ConstructionItemUpdateInfo;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;

import jakarta.persistence.CascadeType;
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
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
@Table(indexes = {
        @Index(columnList = "created_at")
})
public class DailyReportOutsourcingConstructionGroup extends BaseEntity {
    private static final String SEQUENCE_NAME = "daily_report_outsourcing_construction_group_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.DAILY_REPORT_ID, nullable = false)
    private DailyReport dailyReport; // 출역일보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.OUTSOURCING_COMPANY_ID)
    private OutsourcingCompany outsourcingCompany; // 외주업체

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.OUTSOURCING_COMPANY_CONTRACT_CONSTRUCTION_GROUP_ID)
    private OutsourcingCompanyContractConstructionGroup outsourcingCompanyContractConstructionGroup; // 외주업체계약 공사항목 그룹

    @Builder.Default
    @OneToMany(mappedBy = "outsourcingConstructionGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyReportOutsourcingConstruction> constructions = new ArrayList<>(); // 공사항목 목록

    /**
     * 요청 객체로부터 엔티티를 업데이트합니다.
     */
    public void updateFrom(final ConstructionGroupUpdateInfo request,
            final OutsourcingCompany outsourcingCompany,
            final OutsourcingCompanyContractConstructionGroup outsourcingCompanyContractConstructionGroup,
            final OutsourcingCompanyContractConstructionService outsourcingCompanyContractConstructionService) {
        this.outsourcingCompany = outsourcingCompany;
        this.outsourcingCompanyContractConstructionGroup = outsourcingCompanyContractConstructionGroup;

        // 공사항목 업데이트
        updateConstructions(request.items(), outsourcingCompanyContractConstructionService);
    }

    /**
     * 공사항목 목록을 업데이트합니다.
     */
    private void updateConstructions(
            final List<ConstructionItemUpdateInfo> constructionItemRequests,
            final OutsourcingCompanyContractConstructionService outsourcingCompanyContractConstructionService) {
        if (constructionItemRequests == null) {
            return;
        }

        // EntitySyncUtils.syncList를 사용하여 공사항목 동기화
        EntitySyncUtils.syncList(
                this.constructions,
                constructionItemRequests,
                (final ConstructionItemUpdateInfo dto) -> {
                    final OutsourcingCompanyContractConstruction contractConstruction = outsourcingCompanyContractConstructionService
                            .getOutsourcingCompanyContractConstructionByIdOrThrow(
                                    dto.outsourcingCompanyContractConstructionId());

                    return DailyReportOutsourcingConstruction.builder()
                            .outsourcingConstructionGroup(this)
                            .outsourcingCompanyContractConstruction(contractConstruction)
                            .specification(dto.specification())
                            .unit(dto.unit())
                            .quantity(dto.quantity())
                            .contractFileUrl(dto.contractFileUrl())
                            .contractOriginalFileName(dto.contractOriginalFileName())
                            .memo(dto.memo())
                            .build();
                });

        // 기존 공사항목 업데이트
        for (final ConstructionItemUpdateInfo itemInfo : constructionItemRequests) {
            if (itemInfo.id() != null) {
                final OutsourcingCompanyContractConstruction contractConstruction = outsourcingCompanyContractConstructionService
                        .getOutsourcingCompanyContractConstructionByIdOrThrow(
                                itemInfo.outsourcingCompanyContractConstructionId());

                this.constructions.stream()
                        .filter(construction -> construction.getId() != null
                                && construction.getId().equals(itemInfo.id()))
                        .findFirst()
                        .ifPresent(construction -> construction.updateFrom(itemInfo, contractConstruction));
            }
        }
    }
}
