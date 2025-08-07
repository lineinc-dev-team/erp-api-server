package com.lineinc.erp.api.server.application.outsourcing;

import com.lineinc.erp.api.server.common.constant.ValidationMessages;
import com.lineinc.erp.api.server.common.util.EntitySyncUtils;
import com.lineinc.erp.api.server.common.util.JaversUtils;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingChangeHistory;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContact;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingChangeType;
import com.lineinc.erp.api.server.domain.outsourcing.repository.OutsourcingChangeRepository;
import com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.request.OutsourcingCompanyContactCreateRequest;

import java.util.*;
import java.util.stream.Collectors;

import com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.request.OutsourcingCompanyContactUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OutsourcingCompanyContactService {
    private final Javers javers;
    private final OutsourcingChangeRepository outsourcingChangeRepository;

    @Transactional
    public void createOutsourcingCompanyContacts(
            OutsourcingCompany outsourcingCompany,
            List<OutsourcingCompanyContactCreateRequest> requests
    ) {
        if (requests == null || requests.isEmpty()) return;

        long mainCount = requests.stream().filter(OutsourcingCompanyContactCreateRequest::isMain).count();
        if (mainCount != 1) {
            throw new IllegalArgumentException(ValidationMessages.MUST_HAVE_ONE_MAIN_CONTACT);
        }

        List<OutsourcingCompanyContact> contacts = requests.stream()
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
    public void updateOutsourcingCompanyContacts(OutsourcingCompany company, List<OutsourcingCompanyContactUpdateRequest> requests) {
        if (requests != null && !requests.isEmpty()) {
            long mainCount = requests.stream().filter(OutsourcingCompanyContactUpdateRequest::isMain).count();
            if (mainCount != 1) {
                throw new IllegalArgumentException(ValidationMessages.MUST_HAVE_ONE_MAIN_CONTACT);
            }
        }

        List<OutsourcingCompanyContact> beforeContacts = company.getContacts().stream()
                .map(contact -> JaversUtils.createSnapshot(javers, contact, OutsourcingCompanyContact.class))
                .toList();

        EntitySyncUtils.syncList(
                company.getContacts(),
                requests,
                (OutsourcingCompanyContactUpdateRequest dto) -> OutsourcingCompanyContact.builder()
                        .name(dto.name())
                        .department(dto.department())
                        .position(dto.position())
                        .isMain(dto.isMain())
                        .landlineNumber(dto.landlineNumber())
                        .phoneNumber(dto.phoneNumber())
                        .email(dto.email())
                        .memo(dto.memo())
                        .outsourcingCompany(company)
                        .build()
        );

        List<OutsourcingCompanyContact> afterContacts = new ArrayList<>(company.getContacts());

        List<Map<String, String>> allChanges = new ArrayList<>();

        Set<Long> beforeIds = beforeContacts.stream()
                .map(OutsourcingCompanyContact::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (OutsourcingCompanyContact after : afterContacts) {
            if (after.getId() == null || !beforeIds.contains(after.getId())) {
                allChanges.add(JaversUtils.extractAddedEntityChange(javers, after));
            }
        }

        Map<Long, OutsourcingCompanyContact> afterMap = afterContacts.stream()
                .filter(c -> c.getId() != null)
                .collect(Collectors.toMap(OutsourcingCompanyContact::getId, c -> c));

        for (OutsourcingCompanyContact before : beforeContacts) {
            if (before.getId() == null || !afterMap.containsKey(before.getId())) continue;
            OutsourcingCompanyContact after = afterMap.get(before.getId());
            Diff diff = javers.compare(before, after);
            List<Map<String, String>> modified = JaversUtils.extractModifiedChanges(javers, diff);
            allChanges.addAll(modified);
        }

        if (!allChanges.isEmpty()) {
            String json = javers.getJsonConverter().toJson(allChanges);
            OutsourcingChangeHistory history = OutsourcingChangeHistory.builder()
                    .outsourcingCompany(company)
                    .type(OutsourcingChangeType.CONTACT)
                    .changes(json)
                    .build();
            outsourcingChangeRepository.save(history);
        }
    }
}

