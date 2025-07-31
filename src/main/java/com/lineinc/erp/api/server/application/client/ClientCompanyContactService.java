package com.lineinc.erp.api.server.application.client;

import com.lineinc.erp.api.server.domain.client.entity.ClientCompany;
import com.lineinc.erp.api.server.domain.client.entity.ClientCompanyChangeHistory;
import com.lineinc.erp.api.server.domain.client.entity.ClientCompanyContact;
import com.lineinc.erp.api.server.domain.client.repository.ClientCompanyChangeHistoryRepository;
import com.lineinc.erp.api.server.presentation.v1.client.dto.request.ClientCompanyContactCreateRequest;
import com.lineinc.erp.api.server.presentation.v1.client.dto.request.ClientCompanyContactUpdateRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.lineinc.erp.api.server.common.util.EntitySyncUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ClientCompanyContactService {

    private final ClientCompanyChangeHistoryRepository clientCompanyChangeHistoryRepository;

    /**
     * 신규 연락처들을 생성하여 ClientCompany에 추가합니다.
     *
     * @param clientCompany 연락처가 속할 ClientCompany 엔티티
     * @param requests      생성 요청 리스트 (null 또는 빈 리스트면 아무 작업 안 함)
     */
    public void createClientCompanyContacts(ClientCompany clientCompany, List<ClientCompanyContactCreateRequest> requests) {
        if (Objects.isNull(requests) || requests.isEmpty()) return;

        // 요청 리스트를 순회하며 각각 ClientCompanyContact 엔티티 생성 후 연관관계 설정 및 추가
        requests.stream()
                .map(dto -> ClientCompanyContact.builder()
                        .name(dto.name())
                        .position(dto.position())
                        .landlineNumber(dto.landlineNumber())
                        .phoneNumber(dto.phoneNumber())
                        .email(dto.email())
                        .memo(dto.memo())
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
        // 원본 연락처 목록 복사
        List<ClientCompanyContact> originalContacts = new ArrayList<>(clientCompany.getContacts());

        EntitySyncUtils.syncList(
                clientCompany.getContacts(),                    // 기존 연락처 리스트
                requests,                                       // 수정 요청 리스트
                (ClientCompanyContactUpdateRequest dto) ->      // 신규 엔티티 생성 함수
                        ClientCompanyContact.builder()
                                .name(dto.name())
                                .position(dto.position())
                                .landlineNumber(dto.landlineNumber())
                                .phoneNumber(dto.phoneNumber())
                                .email(dto.email())
                                .memo(dto.memo())
                                .clientCompany(clientCompany)
                                .build()
        );

        List<ClientCompanyContact> updatedContacts = clientCompany.getContacts();
        List<String> changes = new ArrayList<>();

        // 감지된 삭제
        for (ClientCompanyContact original : originalContacts) {
            if (original.isDeleted()) {
                changes.add("담당자 삭제: " + original.getName());
            }
        }

        // 감지된 추가
        for (ClientCompanyContact updated : updatedContacts) {
            if (updated.getId() == null) {
                changes.add("담당자 추가: " + updated.getName());
            }
        }

        // 변경 이력 저장 (실제로는 change history repository가 필요함 - 여기선 로그 출력 예시)
        // 변경 이력 저장 - 하나의 row에 통합 기록
        if (!changes.isEmpty()) {
            String combinedChange = String.join("\n", changes);
            clientCompanyChangeHistoryRepository.save(ClientCompanyChangeHistory.builder()
                    .clientCompany(clientCompany)
                    .changeDetail(combinedChange)
                    .build());
        }
    }
}