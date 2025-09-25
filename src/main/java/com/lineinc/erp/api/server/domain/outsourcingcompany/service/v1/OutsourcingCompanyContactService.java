package com.lineinc.erp.api.server.domain.outsourcingcompany.service.v1;

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

import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompanyChangeHistory;
import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompanyContact;
import com.lineinc.erp.api.server.domain.outsourcingcompany.enums.OutsourcingCompanyChangeHistoryType;
import com.lineinc.erp.api.server.domain.outsourcingcompany.repository.OutsourcingCompanyChangeRepository;
import com.lineinc.erp.api.server.domain.user.service.v1.UserService;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyContactCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyContactUpdateRequest;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OutsourcingCompanyContactService {
    private final Javers javers;
    private final OutsourcingCompanyChangeRepository outsourcingCompanyChangeRepository;
    private final UserService userService;

    @Transactional
    public void createOutsourcingCompanyContacts(
            final OutsourcingCompany outsourcingCompany,
            final List<OutsourcingCompanyContactCreateRequest> requests) {
        if (requests == null || requests.isEmpty())
            return;

        final long mainCount = requests.stream().filter(OutsourcingCompanyContactCreateRequest::isMain).count();
        if (mainCount != 1) {
            throw new IllegalArgumentException(ValidationMessages.MUST_HAVE_ONE_MAIN_CONTACT);
        }

        final List<OutsourcingCompanyContact> contacts = requests.stream()
                .map(req -> OutsourcingCompanyContact.builder()
                        .outsourcingCompany(outsourcingCompany)
                        .name(req.name())
                        .department(req.department())
                        .position(req.position())
                        .landlineNumber(req.landlineNumber())
                        .phoneNumber(req.phoneNumber())
                        .email(req.email())
                        .memo(req.memo())
                        .isMain(req.isMain() != null ? req.isMain() : false)
                        .build())
                .collect(Collectors.toList());

        outsourcingCompany.getContacts().addAll(contacts);
    }

    @Transactional
    public void updateOutsourcingCompanyContacts(final OutsourcingCompany company,
            final List<OutsourcingCompanyContactUpdateRequest> requests, final Long userId) {
        if (requests != null && !requests.isEmpty()) {
            final long mainCount = requests.stream().filter(OutsourcingCompanyContactUpdateRequest::isMain).count();
            if (mainCount != 1) {
                throw new IllegalArgumentException(ValidationMessages.MUST_HAVE_ONE_MAIN_CONTACT);
            }
        }

        final List<OutsourcingCompanyContact> beforeContacts = company.getContacts().stream()
                .map(contact -> JaversUtils.createSnapshot(javers, contact, OutsourcingCompanyContact.class))
                .toList();

        EntitySyncUtils.syncList(
                company.getContacts(),
                requests,
                (final OutsourcingCompanyContactUpdateRequest dto) -> OutsourcingCompanyContact.builder()
                        .name(dto.name())
                        .department(dto.department())
                        .position(dto.position())
                        .isMain(dto.isMain())
                        .landlineNumber(dto.landlineNumber())
                        .phoneNumber(dto.phoneNumber())
                        .email(dto.email())
                        .memo(dto.memo())
                        .outsourcingCompany(company)
                        .build());

        final List<OutsourcingCompanyContact> afterContacts = new ArrayList<>(company.getContacts());
        final List<Map<String, String>> allChanges = new ArrayList<>();

        final Set<Long> beforeIds = beforeContacts.stream()
                .map(OutsourcingCompanyContact::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (final OutsourcingCompanyContact after : afterContacts) {
            if (after.getId() == null || !beforeIds.contains(after.getId())) {
                allChanges.add(JaversUtils.extractAddedEntityChange(javers, after));
            }
        }

        final Map<Long, OutsourcingCompanyContact> afterMap = afterContacts.stream()
                .filter(c -> c.getId() != null)
                .collect(Collectors.toMap(OutsourcingCompanyContact::getId, c -> c));

        for (final OutsourcingCompanyContact before : beforeContacts) {
            if (before.getId() == null || !afterMap.containsKey(before.getId()))
                continue;
            final OutsourcingCompanyContact after = afterMap.get(before.getId());
            final Diff diff = javers.compare(before, after);
            final List<Map<String, String>> modified = JaversUtils.extractModifiedChanges(javers, diff);
            allChanges.addAll(modified);
        }

        if (!allChanges.isEmpty()) {
            final String json = javers.getJsonConverter().toJson(allChanges);
            final OutsourcingCompanyChangeHistory history = OutsourcingCompanyChangeHistory.builder()
                    .outsourcingCompany(company)
                    .type(OutsourcingCompanyChangeHistoryType.CONTACT)
                    .changes(json)
                    .user(userService.getUserByIdOrThrow(userId))
                    .build();
            outsourcingCompanyChangeRepository.save(history);
        }
    }
}
