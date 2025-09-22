package com.lineinc.erp.api.server.domain.managementcost.service.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCost;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostChangeHistory;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostFile;
import com.lineinc.erp.api.server.domain.managementcost.enums.ManagementCostChangeHistoryType;
import com.lineinc.erp.api.server.domain.managementcost.repository.ManagementCostChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.managementcost.repository.ManagementCostFileRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostFileCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostFileUpdateRequest;
import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ManagementCostFileService {

    private final ManagementCostFileRepository managementCostFileRepository;
    private final ManagementCostChangeHistoryRepository managementCostChangeHistoryRepository;
    private final Javers javers;

    public void createManagementCostFiles(final List<ManagementCostFileCreateRequest> files,
            final ManagementCost managementCost) {
        if (files != null) {
            for (final ManagementCostFileCreateRequest fileReq : files) {
                final ManagementCostFile file = ManagementCostFile.builder()
                        .managementCost(managementCost)
                        .name(fileReq.name())
                        .fileUrl(fileReq.fileUrl())
                        .originalFileName(fileReq.originalFileName())
                        .memo(fileReq.memo())
                        .build();
                managementCostFileRepository.save(file);
            }
        }
    }

    @Transactional
    public void updateManagementCostFiles(final ManagementCost managementCost,
            final List<ManagementCostFileUpdateRequest> requests) {

        // 변경 전 상태 저장 (Javers 스냅샷) - syncTransientFields 호출 후
        final List<ManagementCostFile> beforeFiles = managementCost.getFiles().stream()
                .map(file -> JaversUtils.createSnapshot(javers, file, ManagementCostFile.class))
                .toList();

        // EntitySyncUtils를 사용하여 파일 목록 동기화
        EntitySyncUtils.syncList(
                managementCost.getFiles(),
                requests,
                (final ManagementCostFileUpdateRequest dto) -> ManagementCostFile.builder()
                        .managementCost(managementCost)
                        .name(dto.name())
                        .fileUrl(dto.fileUrl())
                        .originalFileName(dto.originalFileName())
                        .memo(dto.memo())
                        .build());

        // 변경사항 감지 및 이력 저장
        final List<ManagementCostFile> afterFiles = new ArrayList<>(managementCost.getFiles());
        final List<Map<String, String>> allChanges = new ArrayList<>();

        // 추가된 파일
        final Set<Long> beforeIds = beforeFiles.stream()
                .map(ManagementCostFile::getId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        for (final ManagementCostFile after : afterFiles) {
            if (after.getId() == null || !beforeIds.contains(after.getId())) {
                allChanges.add(JaversUtils.extractAddedEntityChange(javers, after));
            }
        }

        // 수정된 파일
        final Map<Long, ManagementCostFile> afterMap = afterFiles.stream()
                .filter(f -> f.getId() != null)
                .collect(Collectors.toMap(ManagementCostFile::getId, f -> f));

        for (final ManagementCostFile before : beforeFiles) {
            if (before.getId() == null || !afterMap.containsKey(before.getId()))
                continue;

            final ManagementCostFile after = afterMap.get(before.getId());
            final Diff diff = javers.compare(before, after);
            final List<Map<String, String>> modified = JaversUtils.extractModifiedChanges(javers, diff);
            allChanges.addAll(modified);
        }

        // 변경사항이 있다면 이력 저장
        if (!allChanges.isEmpty()) {
            final String changesJson = javers.getJsonConverter().toJson(allChanges);
            final ManagementCostChangeHistory changeHistory = ManagementCostChangeHistory.builder()
                    .managementCost(managementCost)
                    .type(ManagementCostChangeHistoryType.ATTACHMENT)
                    .changes(changesJson)
                    .build();
            managementCostChangeHistoryRepository.save(changeHistory);
        }
    }
}