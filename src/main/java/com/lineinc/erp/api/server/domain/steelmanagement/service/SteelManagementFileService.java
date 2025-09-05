package com.lineinc.erp.api.server.domain.steelmanagement.service;

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

import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagement;
import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagementChangeHistory;
import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagementFile;
import com.lineinc.erp.api.server.domain.steelmanagement.enums.SteelManagementChangeHistoryType;
import com.lineinc.erp.api.server.domain.steelmanagement.repository.SteelManagementChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.steelmanagement.repository.SteelManagementFileRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.request.SteelManagementFileCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.request.SteelManagementFileUpdateRequest;
import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SteelManagementFileService {

    private final SteelManagementFileRepository steelManagementFileRepository;
    private final SteelManagementChangeHistoryRepository steelManagementChangeHistoryRepository;
    private final Javers javers;

    @Transactional
    public void createSteelManagementFiles(SteelManagement steelManagement,
            List<SteelManagementFileCreateRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return;
        }

        List<SteelManagementFile> files = requests.stream()
                .map(request -> SteelManagementFile.builder()
                        .steelManagement(steelManagement)
                        .name(request.name())
                        .fileUrl(request.fileUrl())
                        .originalFileName(request.originalFileName())
                        .memo(request.memo())
                        .build())
                .collect(Collectors.toList());

        steelManagementFileRepository.saveAll(files);
    }

    @Transactional
    public void updateSteelManagementFiles(SteelManagement steelManagement,
            List<SteelManagementFileUpdateRequest> requests) {
        // 1. 현재 파일 목록을 복사해 변경 전 상태(snapshot) 보관
        List<SteelManagementFile> beforeFiles = steelManagement.getFiles().stream()
                .map(file -> JaversUtils.createSnapshot(javers, file, SteelManagementFile.class))
                .toList();

        EntitySyncUtils.syncList(
                steelManagement.getFiles(),
                requests,
                (SteelManagementFileUpdateRequest dto) -> SteelManagementFile.builder()
                        .steelManagement(steelManagement)
                        .name(dto.name())
                        .fileUrl(dto.fileUrl())
                        .originalFileName(dto.originalFileName())
                        .memo(dto.memo())
                        .build());

        // 2. 변경 후 상태와 비교하여 변경 이력 생성
        List<SteelManagementFile> afterFiles = new ArrayList<>(steelManagement.getFiles());
        List<Map<String, String>> allChanges = new ArrayList<>();

        // 추가된 파일
        Set<Long> beforeIds = beforeFiles.stream()
                .map(SteelManagementFile::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (SteelManagementFile after : afterFiles) {
            if (after.getId() == null || !beforeIds.contains(after.getId())) {
                allChanges.add(JaversUtils.extractAddedEntityChange(javers, after));
            }
        }

        // 수정된 파일
        Map<Long, SteelManagementFile> afterMap = afterFiles.stream()
                .filter(f -> f.getId() != null)
                .collect(Collectors.toMap(SteelManagementFile::getId, f -> f));

        for (SteelManagementFile before : beforeFiles) {
            if (before.getId() == null || !afterMap.containsKey(before.getId()))
                continue;

            SteelManagementFile after = afterMap.get(before.getId());
            Diff diff = javers.compare(before, after);
            List<Map<String, String>> modified = JaversUtils.extractModifiedChanges(javers, diff);
            allChanges.addAll(modified);
        }

        // 3. 변경된 이력이 있다면 SteelManagementChangeHistory 엔티티로 저장
        if (!allChanges.isEmpty()) {
            String json = javers.getJsonConverter().toJson(allChanges);
            SteelManagementChangeHistory changeHistory = SteelManagementChangeHistory.builder()
                    .steelManagement(steelManagement)
                    .type(SteelManagementChangeHistoryType.ATTACHMENT)
                    .changes(json)
                    .build();
            steelManagementChangeHistoryRepository.save(changeHistory);
        }
    }
}
