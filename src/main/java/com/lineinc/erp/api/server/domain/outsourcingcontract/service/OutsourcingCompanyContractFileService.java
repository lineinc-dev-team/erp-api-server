package com.lineinc.erp.api.server.domain.outsourcingcontract.service;

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

import com.lineinc.erp.api.server.domain.outsourcingcontract.entity.OutsourcingCompanyContract;
import com.lineinc.erp.api.server.domain.outsourcingcontract.entity.OutsourcingCompanyContractChangeHistory;
import com.lineinc.erp.api.server.domain.outsourcingcontract.entity.OutsourcingCompanyContractFile;
import com.lineinc.erp.api.server.domain.outsourcingcontract.enums.OutsourcingCompanyContractChangeType;
import com.lineinc.erp.api.server.domain.outsourcingcontract.repository.OutsourcingCompanyContractChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.outsourcingcontract.repository.OutsourcingCompanyContractRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyContractFileUpdateRequest;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 외주업체 계약 첨부파일 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OutsourcingCompanyContractFileService {

    private final OutsourcingCompanyContractRepository contractRepository;
    private final OutsourcingCompanyContractChangeHistoryRepository changeHistoryRepository;
    private final Javers javers;

    /**
     * 계약 첨부파일 정보를 수정합니다.
     */
    @Transactional
    public void updateContractFiles(Long contractId, List<OutsourcingCompanyContractFileUpdateRequest> files) {
        log.info("계약 첨부파일 정보 수정: contractId={}, files={}", contractId, files);

        // 1. 계약이 존재하는지 확인
        OutsourcingCompanyContract contract = contractRepository.findById(contractId)
                .orElseThrow(
                        () -> new IllegalArgumentException(ValidationMessages.OUTSOURCING_COMPANY_CONTRACT_NOT_FOUND));

        // 2. 변경 전 스냅샷 생성
        List<OutsourcingCompanyContractFile> beforeFiles = contract.getFiles().stream()
                .map(file -> JaversUtils.createSnapshot(javers, file, OutsourcingCompanyContractFile.class))
                .toList();

        // 3. 첨부파일 정보 동기화 (EntitySyncUtils 사용)
        EntitySyncUtils.syncList(
                contract.getFiles(),
                files,
                (OutsourcingCompanyContractFileUpdateRequest dto) -> OutsourcingCompanyContractFile.builder()
                        .name(dto.name())
                        .fileUrl(dto.fileUrl())
                        .originalFileName(dto.originalFileName())
                        .memo(dto.memo())
                        .outsourcingCompanyContract(contract)
                        .build());

        // 4. 변경사항 추출 및 변경 히스토리 저장
        List<OutsourcingCompanyContractFile> afterFiles = new ArrayList<>(contract.getFiles());
        List<Map<String, String>> allChanges = new ArrayList<>();

        Set<Long> beforeIds = beforeFiles.stream()
                .map(OutsourcingCompanyContractFile::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (OutsourcingCompanyContractFile after : afterFiles) {
            if (after.getId() == null || !beforeIds.contains(after.getId())) {
                allChanges.add(JaversUtils.extractAddedEntityChange(javers, after));
            }
        }

        Map<Long, OutsourcingCompanyContractFile> afterMap = afterFiles.stream()
                .filter(f -> f.getId() != null)
                .collect(Collectors.toMap(OutsourcingCompanyContractFile::getId, f -> f));

        for (OutsourcingCompanyContractFile before : beforeFiles) {
            if (before.getId() == null || !afterMap.containsKey(before.getId()))
                continue;
            OutsourcingCompanyContractFile after = afterMap.get(before.getId());
            Diff diff = javers.compare(before, after);
            List<Map<String, String>> modified = JaversUtils.extractModifiedChanges(javers, diff);
            allChanges.addAll(modified);
        }

        if (!allChanges.isEmpty()) {
            String json = javers.getJsonConverter().toJson(allChanges);
            OutsourcingCompanyContractChangeHistory history = OutsourcingCompanyContractChangeHistory.builder()
                    .outsourcingCompanyContract(contract)
                    .type(OutsourcingCompanyContractChangeType.ATTACHMENT)
                    .changes(json)
                    .build();
            changeHistoryRepository.save(history);
        }
    }
}
