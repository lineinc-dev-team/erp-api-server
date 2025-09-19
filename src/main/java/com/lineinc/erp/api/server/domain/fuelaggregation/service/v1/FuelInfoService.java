package com.lineinc.erp.api.server.domain.fuelaggregation.service.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelAggregation;
import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelAggregationChangeHistory;
import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelInfo;
import com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelAggregationChangeType;
import com.lineinc.erp.api.server.domain.fuelaggregation.repository.FuelAggregationChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcingcompany.service.v1.OutsourcingCompanyService;
import com.lineinc.erp.api.server.domain.outsourcingcontract.entity.OutsourcingCompanyContractDriver;
import com.lineinc.erp.api.server.domain.outsourcingcontract.entity.OutsourcingCompanyContractEquipment;
import com.lineinc.erp.api.server.domain.outsourcingcontract.service.v1.OutsourcingCompanyContractService;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.FuelAggregationUpdateRequest.FuelInfoUpdateRequest;
import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FuelInfoService {

    private final FuelAggregationChangeHistoryRepository fuelAggregationChangeHistoryRepository;
    private final OutsourcingCompanyService outsourcingCompanyService;
    private final OutsourcingCompanyContractService outsourcingCompanyContractService;
    private final Javers javers;

    @Transactional
    public void updateFuelInfos(final FuelAggregation fuelAggregation, final List<FuelInfoUpdateRequest> requests) {
        // 모든 FuelInfo의 transient 필드 동기화
        fuelAggregation.getFuelInfos().forEach(FuelInfo::syncTransientFields);

        // 변경 전 상태 저장 (Javers 스냅샷) - syncTransientFields 호출 후
        final List<FuelInfo> beforeFuelInfos = fuelAggregation.getFuelInfos().stream()
                .map(fuelInfo -> JaversUtils.createSnapshot(javers, fuelInfo, FuelInfo.class))
                .toList();

        // EntitySyncUtils를 사용하여 유류정보 목록 동기화
        EntitySyncUtils.syncList(
                fuelAggregation.getFuelInfos(),
                requests,
                (final FuelInfoUpdateRequest dto) -> {
                    // 업체, 기사, 장비 ID 검증
                    final OutsourcingCompany outsourcingCompany = outsourcingCompanyService
                            .getOutsourcingCompanyByIdOrThrow(dto.outsourcingCompanyId());
                    final OutsourcingCompanyContractDriver driver = outsourcingCompanyContractService
                            .getDriverByIdOrThrow(dto.driverId());
                    final OutsourcingCompanyContractEquipment equipment = outsourcingCompanyContractService
                            .getEquipmentByIdOrThrow(dto.equipmentId());

                    return FuelInfo.builder()
                            .fuelAggregation(fuelAggregation)
                            .outsourcingCompany(outsourcingCompany)
                            .driver(driver)
                            .equipment(equipment)
                            .fuelType(dto.fuelType())
                            .fuelAmount(dto.fuelAmount())
                            .memo(dto.memo())
                            .build();
                });

        // EntitySyncUtils 실행 후, 각 FuelInfo에 실제 엔티티 설정
        for (final FuelInfo fuelInfo : fuelAggregation.getFuelInfos()) {
            if (fuelInfo.getOutsourcingCompanyId() != null) {
                final OutsourcingCompany company = outsourcingCompanyService
                        .getOutsourcingCompanyByIdOrThrow(fuelInfo.getOutsourcingCompanyId());
                final OutsourcingCompanyContractDriver driver = fuelInfo.getDriverId() != null
                        ? outsourcingCompanyContractService.getDriverByIdOrThrow(fuelInfo.getDriverId())
                        : null;
                final OutsourcingCompanyContractEquipment equipment = fuelInfo.getEquipmentId() != null
                        ? outsourcingCompanyContractService.getEquipmentByIdOrThrow(fuelInfo.getEquipmentId())
                        : null;

                fuelInfo.setEntities(company, driver, equipment);
            }
        }

        // 새로 생성된 엔티티들도 transient 필드 동기화
        fuelAggregation.getFuelInfos().forEach(FuelInfo::syncTransientFields);

        // 변경사항 감지 및 이력 저장
        final List<FuelInfo> afterFuelInfos = new ArrayList<>(fuelAggregation.getFuelInfos());
        final List<Map<String, String>> allChanges = new ArrayList<>();

        // 추가된 유류정보
        final Set<Long> beforeIds = beforeFuelInfos.stream()
                .map(FuelInfo::getId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        for (final FuelInfo after : afterFuelInfos) {
            if (after.getId() == null || !beforeIds.contains(after.getId())) {
                allChanges.add(JaversUtils.extractAddedEntityChange(javers, after));
            }
        }

        // 수정된 유류정보
        final Map<Long, FuelInfo> afterMap = afterFuelInfos.stream()
                .filter(f -> f.getId() != null)
                .collect(Collectors.toMap(FuelInfo::getId, f -> f));

        for (final FuelInfo before : beforeFuelInfos) {
            if (before.getId() == null || !afterMap.containsKey(before.getId()))
                continue;

            final FuelInfo after = afterMap.get(before.getId());
            final Diff diff = javers.compare(before, after);
            final List<Map<String, String>> modified = JaversUtils.extractModifiedChanges(javers, diff);
            allChanges.addAll(modified);
        }

        // 변경사항이 있다면 이력 저장
        if (!allChanges.isEmpty()) {
            final String changesJson = javers.getJsonConverter().toJson(allChanges);
            final FuelAggregationChangeHistory changeHistory = FuelAggregationChangeHistory.builder()
                    .fuelAggregation(fuelAggregation)
                    .type(FuelAggregationChangeType.FUEL_INFO)
                    .changes(changesJson)
                    .build();
            fuelAggregationChangeHistoryRepository.save(changeHistory);
        }
    }
}
