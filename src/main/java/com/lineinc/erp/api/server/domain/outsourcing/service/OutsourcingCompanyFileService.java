package com.lineinc.erp.api.server.domain.outsourcing.service;

import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyFile;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingChangeType;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.javers.core.Javers;
import org.javers.core.diff.Diff;

import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingChangeHistory;
import com.lineinc.erp.api.server.domain.outsourcing.repository.OutsourcingChangeRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyFileCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyFileUpdateRequest;

@Service
@RequiredArgsConstructor
public class OutsourcingCompanyFileService {
    private final Javers javers;
    private final OutsourcingChangeRepository outsourcingChangeRepository;

    @Transactional
    public void createOutsourcingCompanyFiles(
            OutsourcingCompany outsourcingCompany,
            List<OutsourcingCompanyFileCreateRequest> requests) {
        if (requests == null || requests.isEmpty())
            return;

        List<OutsourcingCompanyFile> files = requests.stream()
                .map(req -> OutsourcingCompanyFile.builder()
                        .outsourcingCompany(outsourcingCompany)
                        .name(req.name())
                        .fileUrl(req.fileUrl())
                        .originalFileName(req.originalFileName())
                        .type(req.type())
                        .memo(req.memo())
                        .build())
                .collect(Collectors.toList());

        outsourcingCompany.getFiles().addAll(files);
    }

    @Transactional
    public void updateOutsourcingCompanyFiles(
            OutsourcingCompany company,
            List<OutsourcingCompanyFileUpdateRequest> requests) {
        List<OutsourcingCompanyFile> beforeFiles = company.getFiles().stream()
                .map(file -> JaversUtils.createSnapshot(javers, file, OutsourcingCompanyFile.class))
                .toList();

        EntitySyncUtils.syncList(
                company.getFiles(),
                requests,
                (OutsourcingCompanyFileUpdateRequest dto) -> OutsourcingCompanyFile.builder()
                        .outsourcingCompany(company)
                        .name(dto.name())
                        .fileUrl(dto.fileUrl())
                        .originalFileName(dto.originalFileName())
                        .memo(dto.memo())
                        .build());

        List<OutsourcingCompanyFile> afterFiles = new ArrayList<>(company.getFiles());
        List<Map<String, String>> allChanges = new ArrayList<>();

        Set<Long> beforeIds = beforeFiles.stream()
                .map(OutsourcingCompanyFile::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (OutsourcingCompanyFile after : afterFiles) {
            if (after.getId() == null || !beforeIds.contains(after.getId())) {
                allChanges.add(JaversUtils.extractAddedEntityChange(javers, after));
            }
        }

        Map<Long, OutsourcingCompanyFile> afterMap = afterFiles.stream()
                .filter(f -> f.getId() != null)
                .collect(Collectors.toMap(OutsourcingCompanyFile::getId, f -> f));

        for (OutsourcingCompanyFile before : beforeFiles) {
            if (before.getId() == null || !afterMap.containsKey(before.getId()))
                continue;
            OutsourcingCompanyFile after = afterMap.get(before.getId());
            Diff diff = javers.compare(before, after);
            List<Map<String, String>> modified = JaversUtils.extractModifiedChanges(javers, diff);
            allChanges.addAll(modified);
        }

        if (!allChanges.isEmpty()) {
            String json = javers.getJsonConverter().toJson(allChanges);
            OutsourcingChangeHistory history = OutsourcingChangeHistory.builder()
                    .outsourcingCompany(company)
                    .type(OutsourcingChangeType.ATTACHMENT)
                    .changes(json)
                    .build();
            outsourcingChangeRepository.save(history);
        }
    }
}
