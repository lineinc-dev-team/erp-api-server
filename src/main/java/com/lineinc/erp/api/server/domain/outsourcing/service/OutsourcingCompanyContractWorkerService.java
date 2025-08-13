package com.lineinc.erp.api.server.domain.outsourcing.service;

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

import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContract;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractChangeHistory;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractWorker;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyContractChangeType;
import com.lineinc.erp.api.server.domain.outsourcing.repository.OutsourcingCompanyContractChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.outsourcing.repository.OutsourcingCompanyContractRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyContractWorkerUpdateRequest;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 외주업체 계약 인력 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OutsourcingCompanyContractWorkerService {

    private final OutsourcingCompanyContractRepository contractRepository;
    private final OutsourcingCompanyContractChangeHistoryRepository changeHistoryRepository;
    private final Javers javers;

    /**
     * 계약 인력 정보를 수정합니다.
     */
    @Transactional
    public void updateContractWorkers(Long contractId, List<OutsourcingCompanyContractWorkerUpdateRequest> workers) {
        log.info("계약 인력 정보 수정: contractId={}, workers={}", contractId, workers);

        // 1. 계약이 존재하는지 확인
        OutsourcingCompanyContract contract = contractRepository.findById(contractId)
                .orElseThrow(
                        () -> new IllegalArgumentException(ValidationMessages.OUTSOURCING_COMPANY_CONTRACT_NOT_FOUND));

        // 2. 변경 전 스냅샷 생성
        List<OutsourcingCompanyContractWorker> beforeWorkers = contract.getWorkers().stream()
                .map(worker -> JaversUtils.createSnapshot(javers, worker, OutsourcingCompanyContractWorker.class))
                .toList();

        // 3. 인력 정보 동기화 (EntitySyncUtils 사용)
        EntitySyncUtils.syncList(
                contract.getWorkers(),
                workers,
                (OutsourcingCompanyContractWorkerUpdateRequest dto) -> OutsourcingCompanyContractWorker.builder()
                        .name(dto.name())
                        .category(dto.category())
                        .taskDescription(dto.taskDescription())
                        .memo(dto.memo())
                        .outsourcingCompanyContract(contract)
                        .build());

        // 4. 변경사항 추출 및 변경 히스토리 저장
        List<OutsourcingCompanyContractWorker> afterWorkers = new ArrayList<>(contract.getWorkers());
        List<Map<String, String>> allChanges = new ArrayList<>();

        Set<Long> beforeIds = beforeWorkers.stream()
                .map(OutsourcingCompanyContractWorker::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (OutsourcingCompanyContractWorker after : afterWorkers) {
            if (after.getId() == null || !beforeIds.contains(after.getId())) {
                allChanges.add(JaversUtils.extractAddedEntityChange(javers, after));
            }
        }

        Map<Long, OutsourcingCompanyContractWorker> afterMap = afterWorkers.stream()
                .filter(w -> w.getId() != null)
                .collect(Collectors.toMap(OutsourcingCompanyContractWorker::getId, w -> w));

        for (OutsourcingCompanyContractWorker before : beforeWorkers) {
            if (before.getId() == null || !afterMap.containsKey(before.getId()))
                continue;
            OutsourcingCompanyContractWorker after = afterMap.get(before.getId());
            Diff diff = javers.compare(before, after);
            List<Map<String, String>> modified = JaversUtils.extractModifiedChanges(javers, diff);
            allChanges.addAll(modified);
        }

        if (!allChanges.isEmpty()) {
            String json = javers.getJsonConverter().toJson(allChanges);
            OutsourcingCompanyContractChangeHistory history = OutsourcingCompanyContractChangeHistory.builder()
                    .outsourcingCompanyContract(contract)
                    .type(OutsourcingCompanyContractChangeType.WORKER)
                    .changes(json)
                    .build();
            changeHistoryRepository.save(history);
        }
    }
}
