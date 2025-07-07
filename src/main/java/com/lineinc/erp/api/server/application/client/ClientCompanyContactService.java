package com.lineinc.erp.api.server.application.client;

import com.lineinc.erp.api.server.domain.client.entity.ClientCompany;
import com.lineinc.erp.api.server.domain.client.entity.ClientCompanyContact;
import com.lineinc.erp.api.server.presentation.v1.client.dto.request.ClientCompanyContactCreateRequest;
import com.lineinc.erp.api.server.presentation.v1.client.dto.request.ClientCompanyContactUpdateRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.lineinc.erp.api.server.common.util.EntitySyncUtils;

import java.util.List;
import java.util.Objects;

@Service
public class ClientCompanyContactService {

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
        EntitySyncUtils.syncList(
                clientCompany.getContacts(),                    // 기존 연락처 리스트
                requests,                                       // 수정 요청 리스트
                ClientCompanyContact::getId,                    // 엔티티에서 ID 추출 함수
                ClientCompanyContactUpdateRequest::id,          // DTO에서 ID 추출 함수
                (ClientCompanyContactUpdateRequest dto) ->      // 신규 엔티티 생성 함수
                        ClientCompanyContact.builder()
                                .name(dto.name())
                                .position(dto.position())
                                .landlineNumber(dto.landlineNumber())
                                .phoneNumber(dto.phoneNumber())
                                .email(dto.email())
                                .memo(dto.memo())
                                .clientCompany(clientCompany)
                                .build(),
                clientCompany.getContacts()::add                // 새 엔티티 추가 함수
        );
    }
}