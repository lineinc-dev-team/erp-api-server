package com.lineinc.erp.api.server.domain.outsourcing.service;

import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.shared.util.ExcelExportUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingChangeHistory;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingChangeType;
import com.lineinc.erp.api.server.domain.outsourcing.repository.OutsourcingChangeRepository;

import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcing.repository.OutsourcingCompanyRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.DeleteOutsourcingCompaniesRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyChangeHistoryResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyContactResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyDetailResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
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
    public CompanyDetailResponse getOutsourcingCompanyById(Long id) {
        OutsourcingCompany company = outsourcingCompanyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.OUTSOURCING_COMPANY_NOT_FOUND));
        return CompanyDetailResponse.from(company);
    }

    @Transactional
    public void updateOutsourcingCompany(Long id, OutsourcingCompanyUpdateRequest request) {
        OutsourcingCompany company = outsourcingCompanyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.OUTSOURCING_COMPANY_NOT_FOUND));

        if (request.businessNumber() != null &&
                !request.businessNumber().equals(company.getBusinessNumber()) &&
                outsourcingCompanyRepository.existsByBusinessNumber(request.businessNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ValidationMessages.BUSINESS_NUMBER_ALREADY_EXISTS);
        }

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

        if (request.changeHistories() != null && !request.changeHistories().isEmpty()) {
            for (OutsourcingCompanyUpdateRequest.ChangeHistoryRequest historyRequest : request.changeHistories()) {
                outsourcingChangeRepository.findById(historyRequest.id())
                        .filter(history -> history.getOutsourcingCompany().getId().equals(company.getId()))
                        .ifPresent(history -> {
                            history.setMemo(historyRequest.memo());
                        });
            }
        }

        outsourcingCompanyContactService.updateOutsourcingCompanyContacts(company, request.contacts());
        outsourcingCompanyFileService.updateOutsourcingCompanyFiles(company, request.files());
    }

    @Transactional(readOnly = true)
    public Page<CompanyResponse> getAllOutsourcingCompanies(OutsourcingCompanyListRequest request, Pageable pageable) {
        return outsourcingCompanyRepository.findAll(request, pageable);
    }

    @Transactional
    public void deleteOutsourcingCompanies(DeleteOutsourcingCompaniesRequest request) {
        List<OutsourcingCompany> outsourcingCompanies = outsourcingCompanyRepository.findAllById(request.outsourcingCompanyIds());
        if (outsourcingCompanies.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.OUTSOURCING_COMPANY_NOT_FOUND);
        }

        for (OutsourcingCompany company : outsourcingCompanies) {
            company.markAsDeleted();
        }

        outsourcingCompanyRepository.saveAll(outsourcingCompanies);
    }

    @Transactional(readOnly = true)
    public Workbook downloadExcel(
            OutsourcingCompanyListRequest request,
            Sort sort,
            List<String> fields
    ) {
        List<CompanyResponse> companyRespons = outsourcingCompanyRepository.findAllWithoutPaging(request, sort)
                .stream()
                .map(CompanyResponse::from)
                .toList();

        return ExcelExportUtils.generateWorkbook(
                companyRespons,
                fields,
                this::getExcelHeaderName,
                this::getExcelCellValue
        );
    }

    private String getExcelHeaderName(String field) {
        return switch (field) {
            case "id" -> "No.";
            case "name" -> "업체명";
            case "businessNumber" -> "사업자등록번호";
            case "type" -> "구분";
            case "ceoName" -> "대표자명";
            case "address" -> "주소";
            case "phoneNumber" -> "휴대폰";
            case "landlineNumber" -> "전화번호";
            case "contactName" -> "담당자명";
            case "contactPositionAndDepartment" -> "직급/부서";
            case "defaultDeductions" -> "공제항목 기본값";
            case "isActive" -> "사용여부";
            case "createdAtAndUpdatedAt" -> "등록일/수정일";
            case "hasFile" -> "첨부파일 유무";
            case "memo" -> "비고/메모";
            case "email" -> "이메일";
            default -> null;
        };
    }

    private String getExcelCellValue(CompanyResponse company, String field) {
        var mainContact = company.contacts().stream()
                .filter(CompanyContactResponse::isMain)
                .findFirst()
                .orElse(null);

        return switch (field) {
            case "id" -> String.valueOf(company.id());
            case "name" -> company.name();
            case "businessNumber" -> company.businessNumber();
            case "type" -> company.type();
            case "ceoName" -> company.ceoName();
            case "address" -> company.address() + " " + company.detailAddress();
            case "phoneNumber" -> company.phoneNumber();
            case "landlineNumber" -> company.landlineNumber();
            case "contactName" -> mainContact != null ? mainContact.name() : "";
            case "contactPositionAndDepartment" ->
                    mainContact != null ? mainContact.position() + "/" + mainContact.department() : "";
            case "defaultDeductions" -> company.defaultDeductions();
            case "isActive" -> company.isActive() ? "Y" : "N";
            case "createdAtAndUpdatedAt" ->
                    DateTimeFormatUtils.formatKoreaLocalDate(company.createdAt()) + "/" + DateTimeFormatUtils.formatKoreaLocalDate(company.updatedAt());
            case "hasFile" -> company.hasFile() ? "Y" : "N";
            case "memo" -> company.memo();
            case "email" -> company.email();
            default -> null;
        };
    }

    @Transactional(readOnly = true)
    public Slice<CompanyChangeHistoryResponse> getOutsourcingCompanyChangeHistories(Long id, Pageable pageable) {
        OutsourcingCompany company = outsourcingCompanyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.OUTSOURCING_COMPANY_NOT_FOUND));

        Slice<OutsourcingChangeHistory> histories = outsourcingChangeRepository.findAllByOutsourcingCompany(company, pageable);
        return histories.map(CompanyChangeHistoryResponse::from);
    }

    @Transactional(readOnly = true)
    public Slice<CompanyResponse.CompanySimpleResponse> searchByName(String name, Pageable pageable) {
        Slice<OutsourcingCompany> companies;

        if (name == null || name.trim().isEmpty()) {
            companies = outsourcingCompanyRepository.findAllBy(pageable);
        } else {
            companies = outsourcingCompanyRepository.findByNameContainingIgnoreCase(name.trim(), pageable);
        }

        return companies.map(company -> new CompanyResponse.CompanySimpleResponse(
                company.getId(),
                company.getName(),
                company.getBusinessNumber()
        ));
    }
}
