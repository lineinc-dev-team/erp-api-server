package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.service.v1;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContract;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractChangeHistory;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractEquipment;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractSubEquipment;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums.OutsourcingCompanyContractChangeType;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository.OutsourcingCompanyContractChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository.OutsourcingCompanyContractRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request.OutsourcingCompanyContractEquipmentUpdateRequest;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutsourcingCompanyContractEquipmentService {

    private final OutsourcingCompanyContractRepository contractRepository;
    private final OutsourcingCompanyContractChangeHistoryRepository changeHistoryRepository;
    private final Javers javers;

    /**
     * 계약 장비 정보를 수정합니다.
     */
    @Transactional
    public void updateContractEquipments(final Long contractId,
            final List<OutsourcingCompanyContractEquipmentUpdateRequest> equipments) {
        // 1. 계약 존재 확인
        final OutsourcingCompanyContract contract = contractRepository.findById(contractId)
                .orElseThrow(
                        () -> new IllegalArgumentException(ValidationMessages.OUTSOURCING_COMPANY_CONTRACT_NOT_FOUND));

        // 2. 변경 전 스냅샷 생성 (보조장비 포함)
        final List<OutsourcingCompanyContractEquipment> beforeEquipments = contract.getEquipments().stream()
                .map(equipment -> {
                    // 보조장비의 transient 필드 동기화
                    equipment.getSubEquipments().forEach(OutsourcingCompanyContractSubEquipment::syncTransientFields);
                    final OutsourcingCompanyContractEquipment snapshot = JaversUtils.createSnapshot(javers, equipment,
                            OutsourcingCompanyContractEquipment.class);
                    // 보조장비 목록도 깊은 복사로 생성
                    snapshot.setSubEquipments(equipment.getSubEquipments().stream()
                            .map(subEquipment -> JaversUtils.createSnapshot(javers, subEquipment,
                                    OutsourcingCompanyContractSubEquipment.class))
                            .collect(Collectors.toList()));
                    return snapshot;
                })
                .toList();

        // 3. 장비 정보 동기화 (EntitySyncUtils 사용)
        EntitySyncUtils.syncList(
                contract.getEquipments(),
                equipments,
                (final OutsourcingCompanyContractEquipmentUpdateRequest dto) -> {
                    final OutsourcingCompanyContractEquipment equipment = OutsourcingCompanyContractEquipment.builder()
                            .outsourcingCompanyContract(contract)
                            .specification(dto.specification())
                            .vehicleNumber(dto.vehicleNumber())
                            .category(dto.category())
                            .unitPrice(dto.unitPrice())
                            .subtotal(dto.subtotal())
                            .taskDescription(dto.taskDescription())
                            .memo(dto.memo())
                            .build();

                    // 요청에 포함된 보조장비가 있다면 OutsourcingCompanyContractSubEquipment 객체로 변환하여 등록
                    if (dto.subEquipments() != null && !dto.subEquipments().isEmpty()) {
                        equipment.setSubEquipments(dto.subEquipments().stream()
                                .map(subDto -> {
                                    final OutsourcingCompanyContractSubEquipment subEquipment = OutsourcingCompanyContractSubEquipment
                                            .builder()
                                            .equipment(equipment)
                                            .type(subDto.type())
                                            .description(subDto.description())
                                            .memo(subDto.memo())
                                            .build();
                                    return subEquipment;
                                })
                                .collect(Collectors.toList()));
                    }
                    return equipment;
                });

        // 저장을 명시적으로 호출
        contractRepository.save(contract);

        // 4. 변경사항 추출 및 변경 히스토리 저장
        final List<OutsourcingCompanyContractEquipment> afterEquipments = new ArrayList<>(contract.getEquipments());
        final Set<Map<String, String>> allChanges = new LinkedHashSet<>(); // 중복 제거를 위해 Set 사용

        final Set<Long> beforeIds = beforeEquipments.stream()
                .map(OutsourcingCompanyContractEquipment::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (final OutsourcingCompanyContractEquipment after : afterEquipments) {
            if (after.getId() == null || !beforeIds.contains(after.getId())) {
                allChanges.add(JaversUtils.extractAddedEntityChange(javers, after));
            }
        }

        final Map<Long, OutsourcingCompanyContractEquipment> afterMap = afterEquipments.stream()
                .filter(e -> e.getId() != null)
                .collect(Collectors.toMap(OutsourcingCompanyContractEquipment::getId, e -> e));

        for (final OutsourcingCompanyContractEquipment before : beforeEquipments) {
            if (before.getId() == null || !afterMap.containsKey(before.getId()))
                continue;

            final OutsourcingCompanyContractEquipment after = afterMap.get(before.getId());

            // 장비 단위 변경 감지 (보조장비는 제외)
            // 보조장비를 임시로 제거하여 비교
            final List<OutsourcingCompanyContractSubEquipment> beforeSubEquipmentsTemp = before.getSubEquipments();
            final List<OutsourcingCompanyContractSubEquipment> afterSubEquipmentsTemp = after.getSubEquipments();
            before.setSubEquipments(new ArrayList<>());
            after.setSubEquipments(new ArrayList<>());

            final Diff diff = javers.compare(before, after);
            final List<Map<String, String>> modified = JaversUtils.extractModifiedChanges(javers, diff);
            allChanges.addAll(modified);

            // 보조장비 목록 복원
            before.setSubEquipments(beforeSubEquipmentsTemp);
            after.setSubEquipments(afterSubEquipmentsTemp);

            // 장비가 그대로 존재하는 경우에만 보조장비 변경사항 감지
            final List<OutsourcingCompanyContractSubEquipment> beforeSubEquipments = before.getSubEquipments();
            final List<OutsourcingCompanyContractSubEquipment> afterSubEquipments = after.getSubEquipments();
            final Set<Long> beforeSubEquipmentIds = beforeSubEquipments.stream()
                    .map(OutsourcingCompanyContractSubEquipment::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // 보조장비 추가 감지
            for (final OutsourcingCompanyContractSubEquipment afterSubEquipment : afterSubEquipments) {
                if (afterSubEquipment.getId() == null || !beforeSubEquipmentIds.contains(afterSubEquipment.getId())) {
                    allChanges.add(JaversUtils.extractAddedEntityChange(javers, afterSubEquipment));
                }
            }

            // 보조장비 수정 감지
            final Map<Long, OutsourcingCompanyContractSubEquipment> afterSubEquipmentMap = afterSubEquipments.stream()
                    .filter(s -> s.getId() != null)
                    .collect(Collectors.toMap(OutsourcingCompanyContractSubEquipment::getId, s -> s));

            for (final OutsourcingCompanyContractSubEquipment beforeSubEquipment : beforeSubEquipments) {
                if (beforeSubEquipment.getId() == null || !afterSubEquipmentMap.containsKey(beforeSubEquipment.getId()))
                    continue;

                final OutsourcingCompanyContractSubEquipment afterSubEquipment = afterSubEquipmentMap
                        .get(beforeSubEquipment.getId());

                final Diff subEquipmentDiff = javers.compare(beforeSubEquipment, afterSubEquipment);
                final List<Map<String, String>> subEquipmentModified = JaversUtils.extractModifiedChanges(javers,
                        subEquipmentDiff);
                allChanges.addAll(subEquipmentModified);
            }
        }

        // 5. 변경 히스토리 저장
        if (!allChanges.isEmpty()) {
            final String json = javers.getJsonConverter().toJson(allChanges);
            final OutsourcingCompanyContractChangeHistory history = OutsourcingCompanyContractChangeHistory.builder()
                    .outsourcingCompanyContract(contract)
                    .type(OutsourcingCompanyContractChangeType.EQUIPMENT)
                    .changes(json)
                    .build();
            changeHistoryRepository.save(history);
        }
    }
}
