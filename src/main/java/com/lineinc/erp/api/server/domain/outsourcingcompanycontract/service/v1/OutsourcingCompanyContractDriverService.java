package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.service.v1;

import java.util.ArrayList;
import java.util.LinkedHashSet;
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
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractDriver;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractDriverFile;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums.OutsourcingCompanyContractChangeType;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository.OutsourcingCompanyContractChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository.OutsourcingCompanyContractRepository;
import com.lineinc.erp.api.server.domain.user.service.v1.UserService;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request.OutsourcingCompanyContractDriverUpdateRequest;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutsourcingCompanyContractDriverService {

    private final OutsourcingCompanyContractRepository contractRepository;
    private final OutsourcingCompanyContractChangeHistoryRepository changeHistoryRepository;
    private final Javers javers;
    private final UserService userService;

    /**
     * 계약 운전자 정보를 수정합니다.
     */
    @Transactional
    public void updateContractDrivers(final Long contractId,
            final List<OutsourcingCompanyContractDriverUpdateRequest> drivers, final Long userId) {
        // 1. 계약 존재 확인
        final OutsourcingCompanyContract contract = contractRepository.findById(contractId)
                .orElseThrow(
                        () -> new IllegalArgumentException(ValidationMessages.OUTSOURCING_COMPANY_CONTRACT_NOT_FOUND));

        // 2. 변경 전 스냅샷 생성 (파일 포함)
        final List<OutsourcingCompanyContractDriver> beforeDrivers = contract.getDrivers().stream()
                .map(driver -> {
                    // 운전자 파일의 transient 필드 동기화
                    driver.getFiles().forEach(OutsourcingCompanyContractDriverFile::syncTransientFields);

                    final OutsourcingCompanyContractDriver snapshot = JaversUtils.createSnapshot(javers, driver,
                            OutsourcingCompanyContractDriver.class);
                    // 파일 목록도 깊은 복사로 생성
                    snapshot.setFiles(driver.getFiles().stream()
                            .map(file -> JaversUtils.createSnapshot(javers, file,
                                    OutsourcingCompanyContractDriverFile.class))
                            .collect(Collectors.toList()));
                    return snapshot;
                })
                .toList();

        // 3. 운전자 정보 동기화 (EntitySyncUtils 사용)
        EntitySyncUtils.syncList(
                contract.getDrivers(),
                drivers,
                (final OutsourcingCompanyContractDriverUpdateRequest dto) -> {
                    final OutsourcingCompanyContractDriver driver = OutsourcingCompanyContractDriver.builder()
                            .outsourcingCompanyContract(contract)
                            .name(dto.name())
                            .memo(dto.memo())
                            .build();

                    // 요청에 포함된 파일이 있다면 OutsourcingCompanyContractDriverFile 객체로 변환하여 등록
                    if (dto.files() != null && !dto.files().isEmpty()) {
                        driver.setFiles(dto.files().stream()
                                .map(fileDto -> {
                                    final OutsourcingCompanyContractDriverFile file = OutsourcingCompanyContractDriverFile
                                            .builder()
                                            .driver(driver)
                                            .documentType(fileDto.documentType())
                                            .fileUrl(fileDto.fileUrl())
                                            .originalFileName(fileDto.originalFileName())
                                            .build();
                                    // transient 필드 동기화
                                    file.syncTransientFields();
                                    return file;
                                })
                                .collect(Collectors.toList()));
                    }
                    return driver;
                });

        // 저장을 명시적으로 호출
        contractRepository.save(contract);

        // 4. 변경사항 추출 및 변경 히스토리 저장
        final List<OutsourcingCompanyContractDriver> afterDrivers = new ArrayList<>(contract.getDrivers());
        final Set<Map<String, String>> allChanges = new LinkedHashSet<>(); // 중복 제거를 위해 Set 사용

        final Set<Long> beforeIds = beforeDrivers.stream()
                .map(OutsourcingCompanyContractDriver::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (final OutsourcingCompanyContractDriver after : afterDrivers) {
            if (after.getId() == null || !beforeIds.contains(after.getId())) {
                allChanges.add(JaversUtils.extractAddedEntityChange(javers, after));
            }
        }

        final Map<Long, OutsourcingCompanyContractDriver> afterMap = afterDrivers.stream()
                .filter(d -> d.getId() != null)
                .collect(Collectors.toMap(OutsourcingCompanyContractDriver::getId, d -> d));

        for (final OutsourcingCompanyContractDriver before : beforeDrivers) {
            if (before.getId() == null || !afterMap.containsKey(before.getId()))
                continue;

            final OutsourcingCompanyContractDriver after = afterMap.get(before.getId());

            // 운전자 단위 변경 감지 (파일은 포함되지 않음)
            final Diff diff = javers.compare(before, after);
            final List<Map<String, String>> modified = JaversUtils.extractModifiedChanges(javers, diff);
            allChanges.addAll(modified);

            // 운전자가 그대로 존재하는 경우에만 파일 변경사항 감지
            final List<OutsourcingCompanyContractDriverFile> beforeFiles = before.getFiles();
            final List<OutsourcingCompanyContractDriverFile> afterFiles = after.getFiles();
            final Set<Long> beforeFileIds = beforeFiles.stream()
                    .map(OutsourcingCompanyContractDriverFile::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // 파일 추가 감지
            for (final OutsourcingCompanyContractDriverFile afterFile : afterFiles) {
                if (afterFile.getId() == null || !beforeFileIds.contains(afterFile.getId())) {
                    allChanges.add(JaversUtils.extractAddedEntityChange(javers, afterFile));
                }
            }

            // 파일 수정 감지
            final Map<Long, OutsourcingCompanyContractDriverFile> afterFileMap = afterFiles.stream()
                    .filter(f -> f.getId() != null)
                    .collect(Collectors.toMap(OutsourcingCompanyContractDriverFile::getId, f -> f));

            for (final OutsourcingCompanyContractDriverFile beforeFile : beforeFiles) {
                if (beforeFile.getId() == null || !afterFileMap.containsKey(beforeFile.getId()))
                    continue;

                final OutsourcingCompanyContractDriverFile afterFile = afterFileMap.get(beforeFile.getId());
                final Diff fileDiff = javers.compare(beforeFile, afterFile);
                final List<Map<String, String>> fileModified = JaversUtils.extractModifiedChanges(javers, fileDiff);
                allChanges.addAll(fileModified);
            }
        }

        // 5. 변경 히스토리 저장
        if (!allChanges.isEmpty()) {
            final String json = javers.getJsonConverter().toJson(allChanges);
            final OutsourcingCompanyContractChangeHistory history = OutsourcingCompanyContractChangeHistory.builder()
                    .outsourcingCompanyContract(contract)
                    .type(OutsourcingCompanyContractChangeType.DRIVER)
                    .changes(json)
                    .user(userService.getUserByIdOrThrow(userId))
                    .build();
            changeHistoryRepository.save(history);
        }
    }
}
