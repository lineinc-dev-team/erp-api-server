package com.lineinc.erp.api.server.domain.client.service;

import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;
import com.lineinc.erp.api.server.domain.client.enums.ChangeType;
import com.lineinc.erp.api.server.domain.client.entity.ClientCompanyFile;

import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lineinc.erp.api.server.domain.client.entity.ClientCompany;
import com.lineinc.erp.api.server.domain.client.entity.ClientCompanyChangeHistory;
import com.lineinc.erp.api.server.domain.client.repository.CompanyChangeHistoryRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request.ClientCompanyFileCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request.ClientCompanyFileUpdateRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyFileService {
    private final CompanyChangeHistoryRepository companyChangeHistoryRepository;
    private final Javers javers;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void createClientCompanyFile(ClientCompany clientCompany, List<ClientCompanyFileCreateRequest> requests) {
        if (Objects.isNull(requests) || requests.isEmpty()) return;
        requests.stream()
                .map(dto -> ClientCompanyFile.builder()
                        .name(dto.name())
                        .fileUrl(dto.fileUrl())
                        .originalFileName(dto.originalFileName())
                        .memo(dto.memo())
                        .clientCompany(clientCompany)
                        .build())
                .forEach(clientCompany.getFiles()::add);
    }

    /**
     * ClientCompany의 파일들을 요청에 맞게 수정, 생성, 삭제 처리합니다.
     * - 요청 리스트가 null이면 아무 작업도 수행하지 않습니다.
     * - 빈 리스트를 전달하면 기존 파일 전부 soft delete 처리합니다.
     *
     * @param clientCompany 파일이 속한 ClientCompany 엔티티
     * @param requests      수정 요청 리스트 (null일 경우 무시)
     */
    @Transactional
    public void updateClientCompanyFiles(ClientCompany clientCompany, List<ClientCompanyFileUpdateRequest> requests) {
        List<ClientCompanyFile> beforeFiles = clientCompany.getFiles().stream()
                .map(file -> JaversUtils.createSnapshot(javers, file, ClientCompanyFile.class))
                .toList();

        EntitySyncUtils.syncList(
                clientCompany.getFiles(),
                requests,
                (ClientCompanyFileUpdateRequest dto) -> ClientCompanyFile.builder()
                        .name(dto.name())
                        .fileUrl(dto.fileUrl())
                        .originalFileName(dto.originalFileName())
                        .memo(dto.memo())
                        .clientCompany(clientCompany)
                        .build()
        );

        List<ClientCompanyFile> afterFiles = new ArrayList<>(clientCompany.getFiles());
        List<Map<String, String>> allChanges = new ArrayList<>();

        // 추가된 파일
        Set<Long> beforeIds = beforeFiles.stream()
                .map(ClientCompanyFile::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (ClientCompanyFile after : afterFiles) {
            if (after.getId() == null || !beforeIds.contains(after.getId())) {
                allChanges.add(JaversUtils.extractAddedEntityChange(javers, after));
            }
        }
        // 수정된 파일
        Map<Long, ClientCompanyFile> afterMap = afterFiles.stream()
                .filter(c -> c.getId() != null)
                .collect(Collectors.toMap(ClientCompanyFile::getId, c -> c));

        for (ClientCompanyFile before : beforeFiles) {
            if (before.getId() == null || !afterMap.containsKey(before.getId())) continue;

            ClientCompanyFile after = afterMap.get(before.getId());
            Diff diff = javers.compare(before, after);
            List<Map<String, String>> modified = JaversUtils.extractModifiedChanges(javers, diff);
            allChanges.addAll(modified);
        }

        if (!allChanges.isEmpty()) {
            String json = javers.getJsonConverter().toJson(allChanges);
            ClientCompanyChangeHistory history = ClientCompanyChangeHistory.builder()
                    .clientCompany(clientCompany)
                    .type(ChangeType.ATTACHMENT)
                    .changes(json)
                    .build();
            companyChangeHistoryRepository.save(history);
        }

    }
}