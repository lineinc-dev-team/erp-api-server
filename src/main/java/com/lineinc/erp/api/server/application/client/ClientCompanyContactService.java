package com.lineinc.erp.api.server.application.client;

import com.lineinc.erp.api.server.common.constant.ValidationMessages;
import com.lineinc.erp.api.server.common.util.JaversUtils;
import com.lineinc.erp.api.server.domain.client.entity.ClientCompany;
import com.lineinc.erp.api.server.domain.client.entity.ClientCompanyChangeHistory;
import com.lineinc.erp.api.server.domain.client.entity.ClientCompanyContact;
import com.lineinc.erp.api.server.domain.client.enums.ClientCompanyChangeType;
import com.lineinc.erp.api.server.domain.client.repository.ClientCompanyChangeHistoryRepository;
import com.lineinc.erp.api.server.presentation.v1.client.dto.request.ClientCompanyContactCreateRequest;
import com.lineinc.erp.api.server.presentation.v1.client.dto.request.ClientCompanyContactUpdateRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.lineinc.erp.api.server.common.util.EntitySyncUtils;
import org.javers.core.Javers;
import org.javers.core.diff.Diff;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientCompanyContactService {

    private final ClientCompanyChangeHistoryRepository clientCompanyChangeHistoryRepository;
    private final Javers javers;

    /**
     * 신규 연락처들을 생성하여 ClientCompany에 추가합니다.
     *
     * @param clientCompany 연락처가 속할 ClientCompany 엔티티
     * @param requests      생성 요청 리스트 (null 또는 빈 리스트면 아무 작업 안 함)
     */
    public void createClientCompanyContacts(ClientCompany clientCompany, List<ClientCompanyContactCreateRequest> requests) {
        if (Objects.isNull(requests) || requests.isEmpty()) return;

        long mainCount = requests.stream().filter(ClientCompanyContactCreateRequest::isMain).count();
        if (mainCount != 1) {
            throw new IllegalArgumentException(ValidationMessages.MUST_HAVE_ONE_MAIN_CONTACT);
        }

        // 요청 리스트를 순회하며 각각 ClientCompanyContact 엔티티 생성 후 연관관계 설정 및 추가
        requests.stream()
                .map(dto -> ClientCompanyContact.builder()
                        .name(dto.name())
                        .position(dto.position())
                        .department(dto.department())
                        .landlineNumber(dto.landlineNumber())
                        .phoneNumber(dto.phoneNumber())
                        .email(dto.email())
                        .memo(dto.memo())
                        .isMain(dto.isMain())
                        .clientCompany(clientCompany)
                        .build())
                .forEach(clientCompany.getContacts()::add);
    }

    /**
     * ClientCompany의 연락처들을 요청에 맞게 수정, 생성, 삭제 처리합니다.
     * - 요청 리스트가 null이면 아무 작업도 수행하지 않습니다.
     * - 빈 리스트를 전달하면 기존 연락처 전부 soft delete 처리합니다.
     *
     * @param clientCompany 연락처가 속한 ClientCompany 엔티티
     * @param requests      수정 요청 리스트 (null일 경우 무시)
     */
    @Transactional
    public void updateClientCompanyContacts(ClientCompany clientCompany, List<ClientCompanyContactUpdateRequest> requests) {
        if (Objects.nonNull(requests) && !requests.isEmpty()) {
            long mainCount = requests.stream().filter(ClientCompanyContactUpdateRequest::isMain).count();
            if (mainCount != 1) {
                throw new IllegalArgumentException(ValidationMessages.MUST_HAVE_ONE_MAIN_CONTACT);
            }
        }

        List<ClientCompanyContact> beforeContacts = clientCompany.getContacts().stream()
                .map(contact -> JaversUtils.createSnapshot(javers, contact, ClientCompanyContact.class))
                .toList();

        EntitySyncUtils.syncList(
                clientCompany.getContacts(),
                requests,
                (ClientCompanyContactUpdateRequest dto) -> ClientCompanyContact.builder()
                        .name(dto.name())
                        .department(dto.department())
                        .isMain(dto.isMain())
                        .position(dto.position())
                        .landlineNumber(dto.landlineNumber())
                        .phoneNumber(dto.phoneNumber())
                        .email(dto.email())
                        .memo(dto.memo())
                        .clientCompany(clientCompany)
                        .build()
        );

        List<ClientCompanyContact> afterContacts = new ArrayList<>(clientCompany.getContacts());

        List<Map<String, String>> allChanges = new ArrayList<>();

        // 추가된 연락처
        Set<Long> beforeIds = beforeContacts.stream()
                .map(ClientCompanyContact::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (ClientCompanyContact after : afterContacts) {
            if (after.getId() == null || !beforeIds.contains(after.getId())) {
                allChanges.add(JaversUtils.extractAddedEntityChange(javers, after));
            }
        }

        // 수정된 연락처
        Map<Long, ClientCompanyContact> afterMap = afterContacts.stream()
                .filter(c -> c.getId() != null)
                .collect(Collectors.toMap(ClientCompanyContact::getId, c -> c));

        for (ClientCompanyContact before : beforeContacts) {
            if (before.getId() == null || !afterMap.containsKey(before.getId())) continue;

            ClientCompanyContact after = afterMap.get(before.getId());
            Diff diff = javers.compare(before, after);
            List<Map<String, String>> modified = JaversUtils.extractModifiedChanges(javers, diff);
            allChanges.addAll(modified);
        }

        if (!allChanges.isEmpty()) {
            String json = javers.getJsonConverter().toJson(allChanges);
            ClientCompanyChangeHistory history = ClientCompanyChangeHistory.builder()
                    .clientCompany(clientCompany)
                    .type(ClientCompanyChangeType.CONTACT)
                    .changes(json)
                    .build();
            clientCompanyChangeHistoryRepository.save(history);
        }
    }
}