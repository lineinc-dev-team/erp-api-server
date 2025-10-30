package com.lineinc.erp.api.server.domain.aggregation.managementcost.service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCost;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostDetail;
import com.lineinc.erp.api.server.domain.managementcost.enums.ManagementCostItemType;
import com.lineinc.erp.api.server.domain.managementcost.repository.ManagementCostRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request.ManagementCostAggregationRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.ManagementCostAggregationResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.ManagementCostAggregationResponse.BillingDetail;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.ManagementCostAggregationResponse.ManagementCostAggregationItem;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManagementCostAggregationService {

    private final ManagementCostRepository managementCostRepository;

    public ManagementCostAggregationResponse getManagementCostAggregation(
            final ManagementCostAggregationRequest request) {
        final YearMonth ym = YearMonth.parse(request.yearMonth());
        final LocalDate startMonth = ym.atDay(1);
        final LocalDate nextMonthStart = ym.plusMonths(1).atDay(1);
        final OffsetDateTime endExclusive = DateTimeFormatUtils.toUtcStartOfDay(nextMonthStart);

        // GROUP BY: 업체, 관리비 항목(itemType)
        final List<ManagementCost> costs = managementCostRepository
                .findBySiteIdAndSiteProcessIdAndPaymentDateLessThanAndDeletedFalse(
                        request.siteId(),
                        request.siteProcessId(),
                        endExclusive);

        // 각 그룹별로 세부 집계
        final Map<GroupKey, List<ManagementCostDetail>> grouped = new HashMap<>();
        for (final ManagementCost mc : costs) {
            final CompanyResponse.CompanySimpleResponse companySimple = mc.getOutsourcingCompany() != null
                    ? CompanyResponse.CompanySimpleResponse.from(mc.getOutsourcingCompany())
                    : null;
            final var itemType = mc.getItemType();
            final GroupKey key = new GroupKey(companySimple, itemType);
            for (final ManagementCostDetail detail : mc.getDetails()) {
                if (detail.isDeleted())
                    continue;
                grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(detail);
            }
        }

        final List<ManagementCostAggregationItem> items = new ArrayList<>();
        final OffsetDateTime startInclusive = DateTimeFormatUtils.toUtcStartOfDay(startMonth);
        for (final var entry : grouped.entrySet()) {
            // 각 그룹별로 itemTypeDescription(최신) 하나 뽑기
            final String desc = entry.getValue().stream()
                    .map(d -> d.getManagementCost())
                    .filter(Objects::nonNull)
                    .sorted((a, b) -> {
                        if (a.getPaymentDate() == null || b.getPaymentDate() == null)
                            return 0;
                        return b.getPaymentDate().compareTo(a.getPaymentDate()); // 최신 paymentDate 우선
                    })
                    .map(ManagementCost::getItemTypeDescription)
                    .filter(Objects::nonNull)
                    .filter(s -> !s.isBlank())
                    .findFirst().orElse("");
            final var agg = toAggregationItem(entry.getKey(), entry.getValue(), startInclusive, endExclusive, desc);
            if (agg != null)
                items.add(agg);
        }
        return new ManagementCostAggregationResponse(items);
    }

    private ManagementCostAggregationItem toAggregationItem(
            final GroupKey key,
            final List<ManagementCostDetail> details,
            final OffsetDateTime startInclusive,
            final OffsetDateTime endExclusive,
            final String description) {
        long prevSupply = 0, prevVat = 0, prevDeduction = 0;
        long currSupply = 0, currVat = 0, currDeduction = 0;
        for (final var detail : details) {
            final ManagementCost mc = detail.getManagementCost();
            final OffsetDateTime paymentDate = mc.getPaymentDate();
            final long deduction = Boolean.TRUE.equals(detail.getIsDeductible()) ? safe(detail.getTotal()) : 0L;
            if (paymentDate.isBefore(startInclusive)) {
                prevSupply += safe(detail.getSupplyPrice());
                prevVat += safe(detail.getVat());
                prevDeduction += deduction;
            } else if (!paymentDate.isBefore(startInclusive) && paymentDate.isBefore(endExclusive)) {
                currSupply += safe(detail.getSupplyPrice());
                currVat += safe(detail.getVat());
                currDeduction += deduction;
            }
        }
        final long prevTotal = prevSupply + prevVat - prevDeduction;
        final long currTotal = currSupply + currVat - currDeduction;
        if (prevSupply + prevVat + prevDeduction + prevTotal + currSupply + currVat + currDeduction + currTotal == 0)
            return null;
        return new ManagementCostAggregationItem(
                key.company(),
                key.itemType() != null ? key.itemType().getLabel() : null,
                key.itemType(),
                description,
                new BillingDetail(prevSupply, prevVat, prevDeduction, prevTotal),
                new BillingDetail(currSupply, currVat, currDeduction, currTotal));
    }

    private static long safe(final Long v) {
        return v != null ? v : 0L;
    }

    // 업체+itemType 기준
    private record GroupKey(CompanyResponse.CompanySimpleResponse company, ManagementCostItemType itemType) {
    }
}
