package com.lineinc.erp.api.server.application.outsourcing;

import com.lineinc.erp.api.server.common.constant.ValidationMessages;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContact;
import com.lineinc.erp.api.server.presentation.v1.client.dto.request.ClientCompanyContactCreateRequest;
import com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.request.OutsourcingCompanyContactCreateRequest;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OutsourcingCompanyContactService {

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
}
