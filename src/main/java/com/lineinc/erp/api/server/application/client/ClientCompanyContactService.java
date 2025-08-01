package com.lineinc.erp.api.server.application.client;

import com.lineinc.erp.api.server.common.constant.ValidationMessages;
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
import org.javers.core.diff.changetype.ValueChange;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

        // 원본 연락처 목록 복사
        List<ClientCompanyContact> originalContacts = new ArrayList<>(clientCompany.getContacts());

        EntitySyncUtils.syncList(
                clientCompany.getContacts(),                    // 기존 연락처 리스트
                requests,                                       // 수정 요청 리스트
                (ClientCompanyContactUpdateRequest dto) ->      // 신규 엔티티 생성 함수
                        ClientCompanyContact.builder()
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

        List<ClientCompanyContact> updatedContacts = clientCompany.getContacts();
        List<String> changes = new ArrayList<>();

        // 감지된 삭제
        for (ClientCompanyContact original : originalContacts) {
            if (original.isDeleted()) {
                changes.add("담당자 삭제: " + buildContactSnapshot(original));
            }
        }

        // 감지된 추가
        for (ClientCompanyContact updated : updatedContacts) {
            if (updated.getId() == null) {
                changes.add("담당자 추가: " + buildContactSnapshot(updated));
            }
        }

        // 감지된 수정 (Javers diff 사용)
        for (ClientCompanyContact updated : updatedContacts) {
            if (updated.getId() == null) continue;
            ClientCompanyContact original = originalContacts.stream()
                    .filter(o -> o.getId().equals(updated.getId()))
                    .findFirst()
                    .orElse(null);
            if (original != null && !original.isDeleted()) {
                Diff diff = javers.compare(original, updated);
                for (var change : diff.getChanges()) {
                    if (change instanceof ValueChange valueChange) {
                        String propertyName = valueChange.getPropertyName();
                        String label = switch (propertyName) {
                            case "name" -> "이름";
                            case "department" -> "부서";
                            case "position" -> "직책";
                            case "landlineNumber" -> "전화번호";
                            case "phoneNumber" -> "개인 휴대폰";
                            case "email" -> "이메일";
                            case "memo" -> "메모";
                            case "isMain" -> "대표여부";
                            default -> null;
                        };
                        if (label != null) {
                            String left = valueChange.getLeft() == null ? "" : valueChange.getLeft().toString();
                            String right = valueChange.getRight() == null ? "" : valueChange.getRight().toString();
                            if ("isMain".equals(propertyName)) {
                                left = "true".equals(left) ? "Y" : "N";
                                right = "true".equals(right) ? "Y" : "N";
                            }
                            if (!left.equals(right)) {
                                changes.add(String.format("담당자 %s - %s: %s → %s", original.getName(), label, left, right));
                            }
                        }
                    }
                }
            }
        }

        // 변경 이력 저장 (실제로는 change history repository가 필요함 - 여기선 로그 출력 예시)
        // 변경 이력 저장 - 하나의 row에 통합 기록
        if (!changes.isEmpty()) {
            String combinedChange = String.join("\n", changes);
            clientCompanyChangeHistoryRepository.save(ClientCompanyChangeHistory.builder()
                    .clientCompany(clientCompany)
                    .changeDetail(combinedChange)
                    .type(ClientCompanyChangeType.CONTACT)
                    .build());
        }
    }

    private String buildContactSnapshot(ClientCompanyContact contact) {
        return String.format("%s (부서: %s, 직책: %s, 전화번호: %s, 개인 휴대폰: %s, 이메일: %s, 메모: %s, 대표여부: %s)",
                contact.getName(),
                contact.getDepartment(),
                contact.getPosition(),
                contact.getLandlineNumber(),
                contact.getPhoneNumber(),
                contact.getEmail(),
                contact.getMemo(),
                contact.getIsMain() ? "Y" : "N");
    }
}