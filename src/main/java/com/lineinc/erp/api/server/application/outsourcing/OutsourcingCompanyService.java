package com.lineinc.erp.api.server.application.outsourcing;

import com.lineinc.erp.api.server.common.util.JaversUtils;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingChangeHistory;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingChangeType;
import com.lineinc.erp.api.server.domain.outsourcing.repository.OutsourcingChangeRepository;
import com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.response.OutsourcingCompanyDetailResponse;

import com.lineinc.erp.api.server.common.constant.ValidationMessages;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcing.repository.OutsourcingCompanyRepository;
import com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.request.OutsourcingCompanyCreateRequest;
import com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.request.OutsourcingCompanyUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OutsourcingCompanyService {
    private final OutsourcingCompanyRepository outsourcingCompanyRepository;
    private final OutsourcingCompanyContactService outsourcingCompanyContactService;
    private final OutsourcingCompanyFileService outsourcingCompanyFileService;
    private final OutsourcingChangeRepository outsourcingChangeRepository;
    private final Javers javers;

    @Transactional
    public void createOutsourcingCompany(OutsourcingCompanyCreateRequest request) {

        if (outsourcingCompanyRepository.existsByBusinessNumber(request.businessNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ValidationMessages.BUSINESS_NUMBER_ALREADY_EXISTS);
        }

        // 1. OutsourcingCompany 객체 빌드
        OutsourcingCompany outsourcingCompany = OutsourcingCompany.builder()
                .name(request.name())
                .businessNumber(request.businessNumber())
                .type(request.type())
                .typeDescription(request.typeDescription())
                .ceoName(request.ceoName())
                .address(request.address())
                .detailAddress(request.detailAddress())
                .landlineNumber(request.landlineNumber())
                .phoneNumber(request.phoneNumber())
                .email(request.email())
                .defaultDeductions(request.defaultDeductions())
                .defaultDeductionsDescription(request.defaultDeductionsDescription())
                .bankName(request.bankName())
                .accountNumber(request.accountNumber())
                .accountHolder(request.accountHolder())
                .isActive(request.isActive())
                .memo(request.memo())
                .build();

        // 2. 담당자 및 파일 생성, 연관관계 설정
        outsourcingCompanyContactService.createOutsourcingCompanyContacts(outsourcingCompany, request.contacts());
        outsourcingCompanyFileService.createOutsourcingCompanyFiles(outsourcingCompany, request.files());

        // 3. 저장
        outsourcingCompanyRepository.save(outsourcingCompany);
    }

    @Transactional(readOnly = true)
    public OutsourcingCompanyDetailResponse getOutsourcingCompanyById(Long id) {
        OutsourcingCompany company = outsourcingCompanyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.OUTSOURCING_COMPANY_NOT_FOUND));
        return OutsourcingCompanyDetailResponse.from(company);
    }

    @Transactional
    public void updateOutsourcingCompany(Long id, OutsourcingCompanyUpdateRequest request) {
        OutsourcingCompany company = outsourcingCompanyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.OUTSOURCING_COMPANY_NOT_FOUND));

        company.syncTransientFields();
        OutsourcingCompany oldSnapshot = JaversUtils.createSnapshot(javers, company, OutsourcingCompany.class);

        company.updateFrom(request);
        outsourcingCompanyRepository.save(company);

        Diff diff = javers.compare(oldSnapshot, company);
        List<Map<String, String>> simpleChanges = JaversUtils.extractModifiedChanges(javers, diff);
        String changesJson = javers.getJsonConverter().toJson(simpleChanges);

        if (!simpleChanges.isEmpty()) {
            OutsourcingChangeHistory changeHistory = OutsourcingChangeHistory.builder()
                    .outsourcingCompany(company)
                    .type(OutsourcingChangeType.BASIC)
                    .changes(changesJson)
                    .build();
            outsourcingChangeRepository.save(changeHistory);
        }

        outsourcingCompanyContactService.updateOutsourcingCompanyContacts(company, request.contacts());
        outsourcingCompanyFileService.updateOutsourcingCompanyFiles(company, request.files());
    }
}
