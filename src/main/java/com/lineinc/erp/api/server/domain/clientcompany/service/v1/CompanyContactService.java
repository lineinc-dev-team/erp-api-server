package com.lineinc.erp.api.server.domain.clientcompany.service.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.springframework.stereotype.Service;

import com.lineinc.erp.api.server.domain.clientcompany.entity.ClientCompany;
import com.lineinc.erp.api.server.domain.clientcompany.entity.ClientCompanyChangeHistory;
import com.lineinc.erp.api.server.domain.clientcompany.entity.ClientCompanyContact;
import com.lineinc.erp.api.server.domain.clientcompany.enums.ClientCompanyChangeHistoryChangeType;
import com.lineinc.erp.api.server.domain.clientcompany.repository.CompanyChangeHistoryRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request.ClientCompanyContactCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request.ClientCompanyContactUpdateRequest;
import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;
import com.lineinc.erp.api.server.shared.util.ValidationUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyContactService {

    private final CompanyChangeHistoryRepository companyChangeHistoryRepository;
    private final Javers javers;

    public void createClientCompanyContacts(final ClientCompany clientCompany,
            final List<ClientCompanyContactCreateRequest> requests) {
        // 1. 메인 담당자가 정확히 1명 존재하는지 검증
        ValidationUtils.validateMainContactExists(requests, ClientCompanyContactCreateRequest::isMain);

        // 2. 요청 리스트를 순회하며 각각 ClientCompanyContact 엔티티 생성 후 연관관계 설정 및 추가
        requests.stream()
                .map(request -> ClientCompanyContact.createFrom(request, clientCompany))
                .forEach(clientCompany.getContacts()::add);
    }

    @Transactional
    public void updateClientCompanyContacts(final ClientCompany clientCompany,
            final List<ClientCompanyContactUpdateRequest> requests) {
        // 1. 메인 담당자가 정확히 1명 존재하는지 검증
        ValidationUtils.validateMainContactExists(requests, ClientCompanyContactUpdateRequest::isMain);

        // 2. 변경 이력 추적을 위한 기존 담당자 정보 스냅샷 생성
        final List<ClientCompanyContact> beforeContacts = clientCompany.getContacts().stream()
                .map(contact -> JaversUtils.createSnapshot(javers, contact, ClientCompanyContact.class))
                .toList();

        // 3. 기존 담당자 목록과 요청 데이터를 동기화 (추가/수정/삭제)
        EntitySyncUtils.syncList(
                clientCompany.getContacts(),
                requests,
                (final ClientCompanyContactUpdateRequest dto) -> ClientCompanyContact.builder()
                        .name(dto.name())
                        .department(dto.department())
                        .isMain(dto.isMain())
                        .position(dto.position())
                        .landlineNumber(dto.landlineNumber())
                        .phoneNumber(dto.phoneNumber())
                        .email(dto.email())
                        .memo(dto.memo())
                        .clientCompany(clientCompany)
                        .build());

        // 4. 변경 후 담당자 정보 수집
        final List<ClientCompanyContact> afterContacts = new ArrayList<>(clientCompany.getContacts());

        final List<Map<String, String>> allChanges = new ArrayList<>();

        // 추가된 연락처
        final Set<Long> beforeIds = beforeContacts.stream()
                .map(ClientCompanyContact::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (final ClientCompanyContact after : afterContacts) {
            if (after.getId() == null || !beforeIds.contains(after.getId())) {
                allChanges.add(JaversUtils.extractAddedEntityChange(javers, after));
            }
        }

        // 수정된 연락처
        final Map<Long, ClientCompanyContact> afterMap = afterContacts.stream()
                .filter(c -> c.getId() != null)
                .collect(Collectors.toMap(ClientCompanyContact::getId, c -> c));

        for (final ClientCompanyContact before : beforeContacts) {
            if (before.getId() == null || !afterMap.containsKey(before.getId()))
                continue;

            final ClientCompanyContact after = afterMap.get(before.getId());
            final Diff diff = javers.compare(before, after);
            final List<Map<String, String>> modified = JaversUtils.extractModifiedChanges(javers, diff);
            allChanges.addAll(modified);
        }

        if (!allChanges.isEmpty()) {
            final String json = javers.getJsonConverter().toJson(allChanges);
            final ClientCompanyChangeHistory history = ClientCompanyChangeHistory.builder()
                    .clientCompany(clientCompany)
                    .type(ClientCompanyChangeHistoryChangeType.CONTACT)
                    .changes(json)
                    .build();
            companyChangeHistoryRepository.save(history);
        }
    }

}