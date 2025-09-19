package com.lineinc.erp.api.server.domain.labor.service;

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

import com.lineinc.erp.api.server.domain.labor.entity.Labor;
import com.lineinc.erp.api.server.domain.labor.entity.LaborChangeHistory;
import com.lineinc.erp.api.server.domain.labor.entity.LaborFile;
import com.lineinc.erp.api.server.domain.labor.enums.LaborChangeType;
import com.lineinc.erp.api.server.domain.labor.repository.LaborChangeHistoryRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.request.LaborFileUpdateRequest;
import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;

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
    public void updateLaborFiles(final Labor labor, final List<LaborFileUpdateRequest> requests) {
        // 변경 전 상태 저장 (Javers 스냅샷)
        final List<LaborFile> beforeFiles = labor.getFiles().stream()
                .map(file -> JaversUtils.createSnapshot(javers, file, LaborFile.class))
                .toList();

        EntitySyncUtils.syncList(
                labor.getFiles(),
                requests,
                (final LaborFileUpdateRequest dto) -> LaborFile.builder()
                        .labor(labor)
                        .name(dto.name())
                        .fileUrl(dto.fileUrl())
                        .originalFileName(dto.originalFileName())
                        .memo(dto.memo())
                        .build());

        // Javers를 사용하여 변경사항 추적
        final List<LaborFile> afterFiles = new ArrayList<>(labor.getFiles());
        final List<Map<String, String>> allChanges = new ArrayList<>();

        // 추가된 파일
        final Set<Long> beforeIds = beforeFiles.stream()
                .map(LaborFile::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (final LaborFile after : afterFiles) {
            if (after.getId() == null || !beforeIds.contains(after.getId())) {
                allChanges.add(JaversUtils.extractAddedEntityChange(javers, after));
            }
        }

        // 수정된 파일
        final Map<Long, LaborFile> afterMap = afterFiles.stream()
                .filter(f -> f.getId() != null)
                .collect(Collectors.toMap(LaborFile::getId, f -> f));

        for (final LaborFile before : beforeFiles) {
            if (before.getId() != null && afterMap.containsKey(before.getId())) {
                final LaborFile after = afterMap.get(before.getId());
                final Diff diff = javers.compare(before, after);
                if (!diff.getChanges().isEmpty()) {
                    allChanges.addAll(JaversUtils.extractModifiedChanges(javers, diff));
                }
            }
        }

        // 변경사항이 있을 때만 수정이력 생성
        if (!allChanges.isEmpty()) {
            final String changesJson = javers.getJsonConverter().toJson(allChanges);
            final LaborChangeHistory changeHistory = LaborChangeHistory.builder()
                    .labor(labor)
                    .type(LaborChangeType.ATTACHMENT)
                    .changes(changesJson)
                    .build();
            laborChangeHistoryRepository.save(changeHistory);
        }
    }

}
