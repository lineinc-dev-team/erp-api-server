package com.lineinc.erp.api.server.domain.steelmanagement.service.v1;

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
    public void createSteelManagementFiles(final SteelManagement steelManagement,
            final List<SteelManagementFileCreateRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return;
        }

        final List<SteelManagementFile> files = requests.stream()
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
    public void updateSteelManagementFiles(final SteelManagement steelManagement,
            final List<SteelManagementFileUpdateRequest> requests) {
        // 1. 현재 파일 목록을 복사해 변경 전 상태(snapshot) 보관
        final List<SteelManagementFile> beforeFiles = steelManagement.getFiles().stream()
                .map(file -> JaversUtils.createSnapshot(javers, file, SteelManagementFile.class))
                .toList();

        EntitySyncUtils.syncList(
                steelManagement.getFiles(),
                requests,
                (final SteelManagementFileUpdateRequest dto) -> SteelManagementFile.builder()
                        .steelManagement(steelManagement)
                        .name(dto.name())
                        .fileUrl(dto.fileUrl())
                        .originalFileName(dto.originalFileName())
                        .memo(dto.memo())
                        .build());

        // 2. 변경 후 상태와 비교하여 변경 이력 생성
        final List<SteelManagementFile> afterFiles = new ArrayList<>(steelManagement.getFiles());
        final List<Map<String, String>> allChanges = new ArrayList<>();

        // 추가된 파일
        final Set<Long> beforeIds = beforeFiles.stream()
                .map(SteelManagementFile::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (final SteelManagementFile after : afterFiles) {
            if (after.getId() == null || !beforeIds.contains(after.getId())) {
                allChanges.add(JaversUtils.extractAddedEntityChange(javers, after));
            }
        }

        // 수정된 파일
        final Map<Long, SteelManagementFile> afterMap = afterFiles.stream()
                .filter(f -> f.getId() != null)
                .collect(Collectors.toMap(SteelManagementFile::getId, f -> f));

        for (final SteelManagementFile before : beforeFiles) {
            if (before.getId() == null || !afterMap.containsKey(before.getId()))
                continue;

            final SteelManagementFile after = afterMap.get(before.getId());
            final Diff diff = javers.compare(before, after);
            final List<Map<String, String>> modified = JaversUtils.extractModifiedChanges(javers, diff);
            allChanges.addAll(modified);
        }

        // 3. 변경된 이력이 있다면 SteelManagementChangeHistory 엔티티로 저장
        if (!allChanges.isEmpty()) {
            final String json = javers.getJsonConverter().toJson(allChanges);
            final SteelManagementChangeHistory changeHistory = SteelManagementChangeHistory.builder()
                    .steelManagement(steelManagement)
                    .type(SteelManagementChangeHistoryType.ATTACHMENT)
                    .changes(json)
                    .build();
            steelManagementChangeHistoryRepository.save(changeHistory);
        }
    }
}
