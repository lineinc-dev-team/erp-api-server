package com.lineinc.erp.api.server.domain.labormanagement.service;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.labormanagement.entity.Labor;
import com.lineinc.erp.api.server.domain.labormanagement.entity.LaborFile;
import com.lineinc.erp.api.server.domain.labormanagement.entity.LaborChangeHistory;
import com.lineinc.erp.api.server.domain.labormanagement.enums.LaborChangeType;
import com.lineinc.erp.api.server.domain.labormanagement.repository.LaborChangeHistoryRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.request.LaborFileUpdateRequest;
import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;
import org.javers.core.Javers;
import org.javers.core.diff.Diff;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LaborFileService {

    private final LaborChangeHistoryRepository laborChangeHistoryRepository;
    private final Javers javers;

    /**
     * 인력정보 첨부파일 수정
     */
    @Transactional
    public void updateLaborFiles(Labor labor, List<LaborFileUpdateRequest> requests) {
        // 변경 전 상태 저장 (Javers 스냅샷)
        List<LaborFile> beforeFiles = labor.getFiles().stream()
                .map(file -> JaversUtils.createSnapshot(javers, file, LaborFile.class))
                .toList();

        EntitySyncUtils.syncList(
                labor.getFiles(),
                requests,
                (LaborFileUpdateRequest dto) -> LaborFile.builder()
                        .labor(labor)
                        .name(dto.name())
                        .fileUrl(dto.fileUrl())
                        .originalFileName(dto.originalFileName())
                        .memo(dto.memo())
                        .build());

        // Javers를 사용하여 변경사항 추적
        List<LaborFile> afterFiles = new ArrayList<>(labor.getFiles());
        List<Map<String, String>> allChanges = new ArrayList<>();

        // 추가된 파일
        Set<Long> beforeIds = beforeFiles.stream()
                .map(LaborFile::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (LaborFile after : afterFiles) {
            if (after.getId() == null || !beforeIds.contains(after.getId())) {
                allChanges.add(JaversUtils.extractAddedEntityChange(javers, after));
            }
        }

        // 수정된 파일
        Map<Long, LaborFile> afterMap = afterFiles.stream()
                .filter(f -> f.getId() != null)
                .collect(Collectors.toMap(LaborFile::getId, f -> f));

        for (LaborFile before : beforeFiles) {
            if (before.getId() != null && afterMap.containsKey(before.getId())) {
                LaborFile after = afterMap.get(before.getId());
                Diff diff = javers.compare(before, after);
                if (!diff.getChanges().isEmpty()) {
                    allChanges.addAll(JaversUtils.extractModifiedChanges(javers, diff));
                }
            }
        }

        // 변경사항이 있을 때만 수정이력 생성
        if (!allChanges.isEmpty()) {
            String changesJson = javers.getJsonConverter().toJson(allChanges);
            LaborChangeHistory changeHistory = LaborChangeHistory.builder()
                    .labor(labor)
                    .type(LaborChangeType.ATTACHMENT)
                    .changes(changesJson)
                    .build();
            laborChangeHistoryRepository.save(changeHistory);
        }
    }

}
