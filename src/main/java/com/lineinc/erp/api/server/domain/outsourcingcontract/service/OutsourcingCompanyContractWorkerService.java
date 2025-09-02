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
import com.lineinc.erp.api.server.domain.outsourcingcontract.entity.OutsourcingCompanyContractWorker;
import com.lineinc.erp.api.server.domain.outsourcingcontract.entity.OutsourcingCompanyContractWorkerFile;
import com.lineinc.erp.api.server.domain.outsourcingcontract.enums.OutsourcingCompanyContractChangeType;
import com.lineinc.erp.api.server.domain.outsourcingcontract.repository.OutsourcingCompanyContractChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.outsourcingcontract.repository.OutsourcingCompanyContractRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request.OutsourcingCompanyContractWorkerUpdateRequest;
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
        // 1. 계약이 존재하는지 확인
        OutsourcingCompanyContract contract = contractRepository.findById(contractId)
                .orElseThrow(
                        () -> new IllegalArgumentException(ValidationMessages.OUTSOURCING_COMPANY_CONTRACT_NOT_FOUND));

        // 2. 변경 전 스냅샷 생성 (파일 포함)
        List<OutsourcingCompanyContractWorker> beforeWorkers = contract.getWorkers().stream()
                .map(worker -> {
                    OutsourcingCompanyContractWorker snapshot = JaversUtils.createSnapshot(javers, worker,
                            OutsourcingCompanyContractWorker.class);
                    return snapshot;
                })
                .toList();

        // 3. 인력 정보 동기화 (EntitySyncUtils 사용)
        EntitySyncUtils.syncList(
                contract.getWorkers(),
                workers,
                (OutsourcingCompanyContractWorkerUpdateRequest dto) -> {
                    OutsourcingCompanyContractWorker worker = OutsourcingCompanyContractWorker.builder()
                            .name(dto.name())
                            .category(dto.category())
                            .taskDescription(dto.taskDescription())
                            .memo(dto.memo())
                            .outsourcingCompanyContract(contract)
                            .build();

                    // 요청에 포함된 파일이 있다면 OutsourcingCompanyContractWorkerFile 객체로 변환하여 등록
                    if (dto.files() != null && !dto.files().isEmpty()) {
                        worker.setFiles(dto.files().stream()
                                .map(fileDto -> OutsourcingCompanyContractWorkerFile.builder()
                                        .worker(worker)
                                        .fileUrl(fileDto.fileUrl())
                                        .originalFileName(fileDto.originalFileName())
                                        .build())
                                .collect(Collectors.toList()));
                    }
                    return worker;
                });

        // 저장을 명시적으로 호출
        contractRepository.save(contract);

        // 4. 변경사항 추출 및 변경 히스토리 저장
        List<OutsourcingCompanyContractWorker> afterWorkers = new ArrayList<>(contract.getWorkers());
        Set<Map<String, String>> allChanges = new LinkedHashSet<>(); // 중복 제거를 위해 Set 사용

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

            // 인력 단위 변경 감지 (파일은 포함되지 않음)
            Diff diff = javers.compare(before, after);
            List<Map<String, String>> modified = JaversUtils.extractModifiedChanges(javers, diff);
            allChanges.addAll(modified);

            // 워커가 그대로 존재하는 경우에만 파일 변경사항 감지
            List<OutsourcingCompanyContractWorkerFile> beforeFiles = before.getFiles();
            List<OutsourcingCompanyContractWorkerFile> afterFiles = after.getFiles();
            Set<Long> beforeFileIds = beforeFiles.stream()
                    .map(OutsourcingCompanyContractWorkerFile::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // 파일 추가 감지
            for (OutsourcingCompanyContractWorkerFile afterFile : afterFiles) {
                if (afterFile.getId() == null || !beforeFileIds.contains(afterFile.getId())) {
                    allChanges.add(JaversUtils.extractAddedEntityChange(javers, afterFile));
                }
            }

            // 파일 수정 감지
            Map<Long, OutsourcingCompanyContractWorkerFile> afterFileMap = afterFiles.stream()
                    .filter(f -> f.getId() != null)
                    .collect(Collectors.toMap(OutsourcingCompanyContractWorkerFile::getId, f -> f));

            for (OutsourcingCompanyContractWorkerFile beforeFile : beforeFiles) {
                if (beforeFile.getId() == null || !afterFileMap.containsKey(beforeFile.getId()))
                    continue;

                OutsourcingCompanyContractWorkerFile afterFile = afterFileMap.get(beforeFile.getId());
                Diff fileDiff = javers.compare(beforeFile, afterFile);
                List<Map<String, String>> fileModified = JaversUtils.extractModifiedChanges(javers, fileDiff);
                allChanges.addAll(fileModified);
            }
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
