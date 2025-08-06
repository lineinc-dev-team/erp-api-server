package com.lineinc.erp.api.server.application.outsourcing;

import com.lineinc.erp.api.server.common.constant.ValidationMessages;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcing.repository.OutsourcingCompanyRepository;
import com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.request.OutsourcingCompanyCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class OutsourcingCompanyService {
    private final OutsourcingCompanyRepository outsourcingCompanyRepository;
    private final OutsourcingCompanyContactService outsourcingCompanyContactService;
    private final OutsourcingCompanyFileService outsourcingCompanyFileService;

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
                .build();

        // 2. 담당자 및 파일 생성, 연관관계 설정
        outsourcingCompanyContactService.createOutsourcingCompanyContacts(outsourcingCompany, request.contacts());
        outsourcingCompanyFileService.createOutsourcingCompanyFiles(outsourcingCompany, request.files());

        // 3. 저장
        outsourcingCompanyRepository.save(outsourcingCompany);
    }
}
