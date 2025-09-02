package com.lineinc.erp.api.server.domain.outsourcingcontract.service;

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

import com.lineinc.erp.api.server.domain.outsourcingcontract.entity.OutsourcingCompanyContract;
import com.lineinc.erp.api.server.domain.outsourcingcontract.entity.OutsourcingCompanyContractChangeHistory;
import com.lineinc.erp.api.server.domain.outsourcingcontract.entity.OutsourcingCompanyContractConstruction;
import com.lineinc.erp.api.server.domain.outsourcingcontract.enums.OutsourcingCompanyContractChangeType;
import com.lineinc.erp.api.server.domain.outsourcingcontract.repository.OutsourcingCompanyContractChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.outsourcingcontract.repository.OutsourcingCompanyContractRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request.OutsourcingCompanyContractConstructionUpdateRequest;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutsourcingCompanyContractConstructionService {

    private final OutsourcingCompanyContractRepository contractRepository;
    private final OutsourcingCompanyContractChangeHistoryRepository changeHistoryRepository;
    private final Javers javers;

    /**
     * 계약 공사항목 정보를 수정합니다.
     */
    @Transactional
    public void updateContractConstructions(Long contractId,
            List<OutsourcingCompanyContractConstructionUpdateRequest> constructions) {
        // 1. 계약 존재 확인
        OutsourcingCompanyContract contract = contractRepository.findById(contractId)
                .orElseThrow(
                        () -> new IllegalArgumentException(ValidationMessages.OUTSOURCING_COMPANY_CONTRACT_NOT_FOUND));

        // 2. 변경 전 스냅샷 생성
        List<OutsourcingCompanyContractConstruction> beforeConstructions = contract.getConstructions().stream()
                .map(construction -> {
                    OutsourcingCompanyContractConstruction snapshot = JaversUtils.createSnapshot(javers, construction,
                            OutsourcingCompanyContractConstruction.class);
                    return snapshot;
                })
                .toList();

        // 3. 공사항목 정보 동기화 (EntitySyncUtils 사용)
        EntitySyncUtils.syncList(
                contract.getConstructions(),
                constructions,
                (OutsourcingCompanyContractConstructionUpdateRequest dto) -> {
                    OutsourcingCompanyContractConstruction construction = OutsourcingCompanyContractConstruction
                            .builder()
                            .outsourcingCompanyContract(contract)
                            .item(dto.item())
                            .specification(dto.specification())
                            .unit(dto.unit())
                            .unitPrice(dto.unitPrice())
                            .contractQuantity(dto.contractQuantity())
                            .contractPrice(dto.contractPrice())
                            .outsourcingContractQuantity(dto.outsourcingContractQuantity())
                            .outsourcingContractPrice(dto.outsourcingContractPrice())
                            .memo(dto.memo())
                            .build();
                    return construction;
                });

        // 저장을 명시적으로 호출
        contractRepository.save(contract);

        // 4. 변경사항 추출 및 변경 히스토리 저장
        List<OutsourcingCompanyContractConstruction> afterConstructions = new ArrayList<>(contract.getConstructions());
        Set<Map<String, String>> allChanges = new LinkedHashSet<>(); // 중복 제거를 위해 Set 사용

        Set<Long> beforeIds = beforeConstructions.stream()
                .map(OutsourcingCompanyContractConstruction::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (OutsourcingCompanyContractConstruction after : afterConstructions) {
            if (after.getId() == null || !beforeIds.contains(after.getId())) {
                allChanges.add(JaversUtils.extractAddedEntityChange(javers, after));
            }
        }

        Map<Long, OutsourcingCompanyContractConstruction> afterMap = afterConstructions.stream()
                .filter(c -> c.getId() != null)
                .collect(Collectors.toMap(OutsourcingCompanyContractConstruction::getId, c -> c));

        for (OutsourcingCompanyContractConstruction before : beforeConstructions) {
            if (before.getId() == null || !afterMap.containsKey(before.getId()))
                continue;

            OutsourcingCompanyContractConstruction after = afterMap.get(before.getId());

            // 공사항목 단위 변경 감지
            Diff diff = javers.compare(before, after);
            List<Map<String, String>> modified = JaversUtils.extractModifiedChanges(javers, diff);
            allChanges.addAll(modified);
        }

        // 5. 변경 히스토리 저장
        if (!allChanges.isEmpty()) {
            String json = javers.getJsonConverter().toJson(allChanges);
            OutsourcingCompanyContractChangeHistory history = OutsourcingCompanyContractChangeHistory.builder()
                    .outsourcingCompanyContract(contract)
                    .type(OutsourcingCompanyContractChangeType.CONSTRUCTION)
                    .changes(json)
                    .build();
            changeHistoryRepository.save(history);
        }
    }
}
