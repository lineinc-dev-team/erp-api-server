package com.lineinc.erp.api.server.domain.outsourcingcontract.service.v1;

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

import com.lineinc.erp.api.server.domain.outsourcingcontract.entity.OutsourcingCompanyContract;
import com.lineinc.erp.api.server.domain.outsourcingcontract.entity.OutsourcingCompanyContractChangeHistory;
import com.lineinc.erp.api.server.domain.outsourcingcontract.entity.OutsourcingCompanyContractContact;
import com.lineinc.erp.api.server.domain.outsourcingcontract.enums.OutsourcingCompanyContractChangeType;
import com.lineinc.erp.api.server.domain.outsourcingcontract.repository.OutsourcingCompanyContractChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.outsourcingcontract.repository.OutsourcingCompanyContractRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request.OutsourcingCompanyContractContactUpdateRequest;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 외주업체 계약 담당자 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OutsourcingCompanyContractContactService {

    private final OutsourcingCompanyContractRepository contractRepository;
    private final OutsourcingCompanyContractChangeHistoryRepository changeHistoryRepository;
    private final Javers javers;

    /**
     * 계약 담당자 정보를 수정합니다.
     */
    @Transactional
    public void updateContractContacts(final Long contractId,
            final List<OutsourcingCompanyContractContactUpdateRequest> contacts) {

        // 1. 계약이 존재하는지 확인
        final OutsourcingCompanyContract contract = contractRepository.findById(contractId)
                .orElseThrow(
                        () -> new IllegalArgumentException(ValidationMessages.OUTSOURCING_COMPANY_CONTRACT_NOT_FOUND));

        // 2. 변경 전 스냅샷 생성
        final List<OutsourcingCompanyContractContact> beforeContacts = contract.getContacts().stream()
                .map(contact -> JaversUtils.createSnapshot(javers, contact, OutsourcingCompanyContractContact.class))
                .toList();

        // 3. 담당자 정보 동기화 (EntitySyncUtils 사용)
        EntitySyncUtils.syncList(
                contract.getContacts(),
                contacts,
                (final OutsourcingCompanyContractContactUpdateRequest dto) -> OutsourcingCompanyContractContact
                        .builder()
                        .name(dto.name())
                        .department(dto.department())
                        .position(dto.position())
                        .landlineNumber(dto.landlineNumber())
                        .phoneNumber(dto.phoneNumber())
                        .email(dto.email())
                        .memo(dto.memo())
                        .outsourcingCompanyContract(contract)
                        .build());

        // 4. 변경사항 추출 및 변경 히스토리 저장
        final List<OutsourcingCompanyContractContact> afterContacts = new ArrayList<>(contract.getContacts());
        final List<Map<String, String>> allChanges = new ArrayList<>();

        final Set<Long> beforeIds = beforeContacts.stream()
                .map(OutsourcingCompanyContractContact::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (final OutsourcingCompanyContractContact after : afterContacts) {
            if (after.getId() == null || !beforeIds.contains(after.getId())) {
                allChanges.add(JaversUtils.extractAddedEntityChange(javers, after));
            }
        }

        final Map<Long, OutsourcingCompanyContractContact> afterMap = afterContacts.stream()
                .filter(c -> c.getId() != null)
                .collect(Collectors.toMap(OutsourcingCompanyContractContact::getId, c -> c));

        for (final OutsourcingCompanyContractContact before : beforeContacts) {
            if (before.getId() == null || !afterMap.containsKey(before.getId()))
                continue;
            final OutsourcingCompanyContractContact after = afterMap.get(before.getId());
            final Diff diff = javers.compare(before, after);
            final List<Map<String, String>> modified = JaversUtils.extractModifiedChanges(javers, diff);
            allChanges.addAll(modified);
        }

        if (!allChanges.isEmpty()) {
            final String json = javers.getJsonConverter().toJson(allChanges);
            final OutsourcingCompanyContractChangeHistory history = OutsourcingCompanyContractChangeHistory.builder()
                    .outsourcingCompanyContract(contract)
                    .type(OutsourcingCompanyContractChangeType.CONTACT)
                    .changes(json)
                    .build();
            changeHistoryRepository.save(history);
        }
    }

}
