package com.lineinc.erp.api.server.domain.materialmanagement.service.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.springframework.stereotype.Service;

import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagement;
import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagementChangeHistory;
import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagementFile;
import com.lineinc.erp.api.server.domain.materialmanagement.enums.MaterialManagementChangeHistoryType;
import com.lineinc.erp.api.server.domain.materialmanagement.repository.MaterialManagementChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.request.MaterialManagementFileCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.request.MaterialManagementFileUpdateRequest;
import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MaterialManagementFileService {

    private final Javers javers;
    private final MaterialManagementChangeHistoryRepository changeHistoryRepository;

    @Transactional
    public void createMaterialFileManagement(
            final MaterialManagement materialManagement,
            final List<MaterialManagementFileCreateRequest> requests) {

        if (requests == null || requests.isEmpty()) {
            return;
        }

        requests.stream()
                .map(file -> MaterialManagementFile.builder()
                        .materialManagement(materialManagement)
                        .name(file.name())
                        .fileUrl(file.fileUrl())
                        .originalFileName(file.originalFileName())
                        .memo(file.memo())
                        .build())
                .forEach(materialManagement.getFiles()::add);
    }

    @Transactional
    public void updateMaterialManagementFiles(
            final MaterialManagement materialManagement,
            final List<MaterialManagementFileUpdateRequest> requests, final User user) {
        // 변경 전 상태 저장 (Javers 스냅샷)
        final List<MaterialManagementFile> beforeFiles = materialManagement.getFiles().stream()
                .map(file -> JaversUtils.createSnapshot(javers, file, MaterialManagementFile.class))
                .toList();

        EntitySyncUtils.syncList(
                materialManagement.getFiles(),
                requests,
                (final MaterialManagementFileUpdateRequest dto) -> MaterialManagementFile.builder()
                        .materialManagement(materialManagement)
                        .name(dto.name())
                        .fileUrl(dto.fileUrl())
                        .originalFileName(dto.originalFileName())
                        .memo(dto.memo())
                        .build());

        // 변경사항 추적 및 수정이력 생성
        final List<Map<String, String>> allChanges = new ArrayList<>();

        // 추가된 파일
        final Set<Long> beforeIds = beforeFiles.stream()
                .map(MaterialManagementFile::getId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        for (final MaterialManagementFile after : materialManagement.getFiles()) {
            if (after.getId() == null || !beforeIds.contains(after.getId())) {
                allChanges.add(JaversUtils.extractAddedEntityChange(javers, after));
            }
        }

        // 수정된 파일
        final Map<Long, MaterialManagementFile> afterMap = materialManagement.getFiles().stream()
                .filter(f -> f.getId() != null)
                .collect(Collectors.toMap(MaterialManagementFile::getId, f -> f));

        for (final MaterialManagementFile before : beforeFiles) {
            if (before.getId() == null || !afterMap.containsKey(before.getId()))
                continue;
            final MaterialManagementFile after = afterMap.get(before.getId());
            final Diff diff = javers.compare(before, after);
            final List<Map<String, String>> modified = JaversUtils.extractModifiedChanges(javers, diff);
            allChanges.addAll(modified);
        }

        // 변경사항이 있을 때만 수정이력 생성
        if (!allChanges.isEmpty()) {
            final String json = javers.getJsonConverter().toJson(allChanges);
            final MaterialManagementChangeHistory history = MaterialManagementChangeHistory.builder()
                    .materialManagement(materialManagement)
                    .type(MaterialManagementChangeHistoryType.ATTACHMENT)
                    .changes(json)
                    .user(user)
                    .build();
            changeHistoryRepository.save(history);
        }
    }
}
