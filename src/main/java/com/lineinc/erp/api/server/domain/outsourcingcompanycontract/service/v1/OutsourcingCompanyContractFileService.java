package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.service.v1;

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

import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContract;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractChangeHistory;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractFile;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums.OutsourcingCompanyContractChangeType;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository.OutsourcingCompanyContractChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository.OutsourcingCompanyContractRepository;
import com.lineinc.erp.api.server.domain.user.service.v1.UserService;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request.OutsourcingCompanyContractFileUpdateRequest;
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
    private final UserService userService;

    /**
     * 계약 첨부파일 정보를 수정합니다.
     */
    @Transactional
    public void updateContractFiles(final Long contractId,
            final List<OutsourcingCompanyContractFileUpdateRequest> files, final Long userId) {
        log.info("계약 첨부파일 정보 수정: contractId={}, files={}", contractId, files);

        // 1. 계약이 존재하는지 확인
        final OutsourcingCompanyContract contract = contractRepository.findById(contractId)
                .orElseThrow(
                        () -> new IllegalArgumentException(ValidationMessages.OUTSOURCING_COMPANY_CONTRACT_NOT_FOUND));

        // 2. 변경 전 스냅샷 생성
        final List<OutsourcingCompanyContractFile> beforeFiles = contract.getFiles().stream()
                .map(file -> JaversUtils.createSnapshot(javers, file, OutsourcingCompanyContractFile.class))
                .toList();

        // 3. 첨부파일 정보 동기화 (EntitySyncUtils 사용)
        EntitySyncUtils.syncList(
                contract.getFiles(),
                files,
                (final OutsourcingCompanyContractFileUpdateRequest dto) -> OutsourcingCompanyContractFile.builder()
                        .name(dto.name())
                        .fileUrl(dto.fileUrl())
                        .originalFileName(dto.originalFileName())
                        .type(dto.type())
                        .memo(dto.memo())
                        .outsourcingCompanyContract(contract)
                        .build());

        // 4. 변경사항 추출 및 변경 히스토리 저장
        final List<OutsourcingCompanyContractFile> afterFiles = new ArrayList<>(contract.getFiles());
        final List<Map<String, String>> allChanges = new ArrayList<>();

        final Set<Long> beforeIds = beforeFiles.stream()
                .map(OutsourcingCompanyContractFile::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (final OutsourcingCompanyContractFile after : afterFiles) {
            if (after.getId() == null || !beforeIds.contains(after.getId())) {
                allChanges.add(JaversUtils.extractAddedEntityChange(javers, after));
            }
        }

        final Map<Long, OutsourcingCompanyContractFile> afterMap = afterFiles.stream()
                .filter(f -> f.getId() != null)
                .collect(Collectors.toMap(OutsourcingCompanyContractFile::getId, f -> f));

        for (final OutsourcingCompanyContractFile before : beforeFiles) {
            if (before.getId() == null || !afterMap.containsKey(before.getId()))
                continue;
            final OutsourcingCompanyContractFile after = afterMap.get(before.getId());
            final Diff diff = javers.compare(before, after);
            final List<Map<String, String>> modified = JaversUtils.extractModifiedChanges(javers, diff);
            allChanges.addAll(modified);
        }

        if (!allChanges.isEmpty()) {
            final String json = javers.getJsonConverter().toJson(allChanges);
            final OutsourcingCompanyContractChangeHistory history = OutsourcingCompanyContractChangeHistory.builder()
                    .outsourcingCompanyContract(contract)
                    .type(OutsourcingCompanyContractChangeType.ATTACHMENT)
                    .changes(json)
                    .user(userService.getUserByIdOrThrow(userId))
                    .build();
            changeHistoryRepository.save(history);
        }
    }
}
