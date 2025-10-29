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
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportOutsourcingConstructionUpdateRequest.OutsourcingCompanyUpdateInfo;
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
public class DailyReportOutsourcingCompany extends BaseEntity {
    private static final String SEQUENCE_NAME = "daily_report_outsourcing_company_seq";

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

    @Builder.Default
    @OneToMany(mappedBy = AppConstants.DAILY_REPORT_OUTSOURCING_COMPANY_MAPPED_BY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyReportOutsourcingConstructionGroup> constructionGroups = new ArrayList<>(); // 공사 그룹 목록

    /**
     * 요청 객체로부터 엔티티를 업데이트합니다.
     */
    public void updateFrom(final OutsourcingCompanyUpdateInfo request,
            final OutsourcingCompany outsourcingCompany,
            final OutsourcingCompanyContractConstructionService outsourcingCompanyContractConstructionService) {
        this.outsourcingCompany = outsourcingCompany;

        // 공사 그룹 업데이트
        updateConstructionGroups(request.groups(), outsourcingCompanyContractConstructionService);
    }

    /**
     * 공사 그룹 목록을 업데이트합니다.
     */
    private void updateConstructionGroups(
            final List<ConstructionGroupUpdateInfo> groupRequests,
            final OutsourcingCompanyContractConstructionService outsourcingCompanyContractConstructionService) {
        if (groupRequests == null) {
            return;
        }

        // EntitySyncUtils.syncList를 사용하여 공사 그룹 동기화
        EntitySyncUtils.syncList(
                this.constructionGroups,
                groupRequests,
                (final ConstructionGroupUpdateInfo dto) -> {
                    final OutsourcingCompanyContractConstructionGroup contractConstructionGroup = outsourcingCompanyContractConstructionService
                            .getOutsourcingCompanyContractConstructionGroupByIdOrThrow(
                                    dto.outsourcingCompanyContractConstructionGroupId());

                    final DailyReportOutsourcingConstructionGroup group = DailyReportOutsourcingConstructionGroup
                            .builder()
                            .dailyReportOutsourcingCompany(this)
                            .outsourcingCompanyContractConstructionGroup(contractConstructionGroup)
                            .build();

                    // 공사항목 추가
                    if (dto.items() != null) {
                        for (final ConstructionItemUpdateInfo itemDto : dto.items()) {
                            final OutsourcingCompanyContractConstruction contractConstruction = outsourcingCompanyContractConstructionService
                                    .getOutsourcingCompanyContractConstructionByIdOrThrow(
                                            itemDto.outsourcingCompanyContractConstructionId());

                            final DailyReportOutsourcingConstruction construction = DailyReportOutsourcingConstruction
                                    .builder()
                                    .outsourcingConstructionGroup(group)
                                    .outsourcingCompanyContractConstruction(contractConstruction)
                                    .specification(itemDto.specification())
                                    .unit(itemDto.unit())
                                    .quantity(itemDto.quantity())
                                    .fileUrl(itemDto.fileUrl())
                                    .originalFileName(itemDto.originalFileName())
                                    .memo(itemDto.memo())
                                    .build();

                            group.getConstructions().add(construction);
                        }
                    }

                    return group;
                });

        // 기존 공사 그룹 업데이트
        for (final ConstructionGroupUpdateInfo groupInfo : groupRequests) {
            if (groupInfo.id() != null) {
                final OutsourcingCompanyContractConstructionGroup contractConstructionGroup = outsourcingCompanyContractConstructionService
                        .getOutsourcingCompanyContractConstructionGroupByIdOrThrow(
                                groupInfo.outsourcingCompanyContractConstructionGroupId());

                this.constructionGroups.stream()
                        .filter(group -> group.getId() != null && group.getId().equals(groupInfo.id()))
                        .findFirst()
                        .ifPresent(group -> group.updateFrom(groupInfo, contractConstructionGroup,
                                outsourcingCompanyContractConstructionService));
            }
        }
    }
}
