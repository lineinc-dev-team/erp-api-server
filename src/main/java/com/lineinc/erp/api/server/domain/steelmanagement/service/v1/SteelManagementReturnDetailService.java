package com.lineinc.erp.api.server.domain.steelmanagement.service.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagement;
import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagementChangeHistory;
import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagementReturnDetail;
import com.lineinc.erp.api.server.domain.steelmanagement.enums.SteelManagementChangeHistoryType;
import com.lineinc.erp.api.server.domain.steelmanagement.repository.SteelManagementChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.request.SteelManagementReturnDetailUpdateRequest;
import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SteelManagementReturnDetailService {
    private final SteelManagementChangeHistoryRepository steelManagementChangeHistoryRepository;
    private final Javers javers;

    @Transactional
    public void updateSteelManagementReturnDetails(final SteelManagement steelManagement,
            final List<SteelManagementReturnDetailUpdateRequest> requests, final User user) {
        // 1. 현재 상세 목록을 복사해 변경 전 상태(snapshot) 보관
        final List<SteelManagementReturnDetail> beforeDetails = steelManagement.getReturnDetails().stream()
                .map(detail -> JaversUtils.createSnapshot(javers, detail, SteelManagementReturnDetail.class))
                .toList();

        EntitySyncUtils.syncList(
                steelManagement.getReturnDetails(),
                requests,
                (final SteelManagementReturnDetailUpdateRequest dto) -> SteelManagementReturnDetail.builder()
                        .steelManagement(steelManagement)
                        .standard(dto.standard())
                        .name(dto.name())
                        .unit(dto.unit())
                        .count(dto.count())
                        .length(dto.length())
                        .totalLength(dto.totalLength())
                        .unitWeight(dto.unitWeight())
                        .quantity(dto.quantity())
                        .unitPrice(dto.unitPrice())
                        .supplyPrice(dto.supplyPrice())
                        .vat(dto.vat())
                        .memo(dto.memo())
                        .build());

        // 2. 변경 후 상태와 비교하여 변경 이력 생성
        final List<SteelManagementReturnDetail> afterDetails = new ArrayList<>(steelManagement.getReturnDetails());
        final List<Map<String, String>> allChanges = new ArrayList<>();

        // 추가된 상세 항목
        final Set<Long> beforeIds = beforeDetails.stream()
                .map(SteelManagementReturnDetail::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (final SteelManagementReturnDetail after : afterDetails) {
            if (after.getId() == null || !beforeIds.contains(after.getId())) {
                allChanges.add(JaversUtils.extractAddedEntityChange(javers, after));
            }
        }

        // 수정된 상세 항목
        final Map<Long, SteelManagementReturnDetail> afterMap = afterDetails.stream()
                .filter(d -> d.getId() != null)
                .collect(Collectors.toMap(SteelManagementReturnDetail::getId, d -> d));

        for (final SteelManagementReturnDetail before : beforeDetails) {
            if (before.getId() == null || !afterMap.containsKey(before.getId()))
                continue;

            final SteelManagementReturnDetail after = afterMap.get(before.getId());
            final Diff diff = javers.compare(before, after);
            final List<Map<String, String>> modified = JaversUtils.extractModifiedChanges(javers, diff);
            allChanges.addAll(modified);
        }

        // 3. 변경된 이력이 있다면 SteelManagementChangeHistory 엔티티로 저장
        if (!allChanges.isEmpty()) {
            final String json = javers.getJsonConverter().toJson(allChanges);
            final SteelManagementChangeHistory changeHistory = SteelManagementChangeHistory.builder()
                    .steelManagement(steelManagement)
                    .type(SteelManagementChangeHistoryType.RETURN_DETAIL)
                    .changes(json)
                    .user(user)
                    .build();
            steelManagementChangeHistoryRepository.save(changeHistory);
        }
    }
}
