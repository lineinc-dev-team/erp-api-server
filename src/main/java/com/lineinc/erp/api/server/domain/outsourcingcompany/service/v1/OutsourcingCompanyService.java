package com.lineinc.erp.api.server.domain.outsourcingcompany.service.v1;

import java.util.List;
import java.util.Map;

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

import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompanyChangeHistory;
import com.lineinc.erp.api.server.domain.outsourcingcompany.enums.OutsourcingCompanyChangeHistoryType;
import com.lineinc.erp.api.server.domain.outsourcingcompany.repository.OutsourcingCompanyChangeRepository;
import com.lineinc.erp.api.server.domain.outsourcingcompany.repository.OutsourcingCompanyRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.DeleteOutsourcingCompaniesRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyChangeHistoryResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyContactResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyDetailResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse;
import com.lineinc.erp.api.server.shared.dto.request.ChangeHistoryRequest;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.shared.util.ExcelExportUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OutsourcingCompanyService {
    private final OutsourcingCompanyRepository outsourcingCompanyRepository;
    private final OutsourcingCompanyContactService outsourcingCompanyContactService;
    private final OutsourcingCompanyFileService outsourcingCompanyFileService;
    private final OutsourcingCompanyChangeRepository outsourcingCompanyChangeRepository;
    private final Javers javers;

    @Transactional
    public OutsourcingCompany createOutsourcingCompany(final OutsourcingCompanyCreateRequest request) {

        if (outsourcingCompanyRepository.existsByBusinessNumber(request.businessNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    ValidationMessages.BUSINESS_NUMBER_ALREADY_EXISTS);
        }

        // 1. OutsourcingCompany 객체 빌드
        final OutsourcingCompany outsourcingCompany = OutsourcingCompany.builder()
                .name(request.name())
                .businessNumber(request.businessNumber())
                .type(request.type())
                .typeDescription(request.typeDescription())
                .ceoName(request.ceoName())
                .address(request.address())
                .detailAddress(request.detailAddress())
                .landlineNumber(request.landlineNumber())
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

        final OutsourcingCompanyChangeHistory changeHistory = OutsourcingCompanyChangeHistory.builder()
                .outsourcingCompany(outsourcingCompany)
                .description(ValidationMessages.INITIAL_CREATION)
                .build();
        outsourcingCompanyChangeRepository.save(changeHistory);

        return outsourcingCompanyRepository.save(outsourcingCompany);
    }

    @Transactional(readOnly = true)
    public CompanyDetailResponse getOutsourcingCompanyById(final Long id) {
        final OutsourcingCompany company = outsourcingCompanyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.OUTSOURCING_COMPANY_NOT_FOUND));
        return CompanyDetailResponse.from(company);
    }

    @Transactional(readOnly = true)
    public OutsourcingCompany getOutsourcingCompanyByIdOrThrow(final Long id) {
        return outsourcingCompanyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.OUTSOURCING_COMPANY_NOT_FOUND));
    }

    @Transactional
    public void updateOutsourcingCompany(final Long id, final OutsourcingCompanyUpdateRequest request) {
        final OutsourcingCompany company = outsourcingCompanyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.OUTSOURCING_COMPANY_NOT_FOUND));

        if (request.businessNumber() != null &&
                !request.businessNumber().equals(company.getBusinessNumber()) &&
                outsourcingCompanyRepository.existsByBusinessNumber(request.businessNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    ValidationMessages.BUSINESS_NUMBER_ALREADY_EXISTS);
        }

        company.syncTransientFields();
        final OutsourcingCompany oldSnapshot = JaversUtils.createSnapshot(javers, company, OutsourcingCompany.class);

        company.updateFrom(request);
        outsourcingCompanyRepository.save(company);

        final Diff diff = javers.compare(oldSnapshot, company);
        final List<Map<String, String>> simpleChanges = JaversUtils.extractModifiedChanges(javers, diff);
        final String changesJson = javers.getJsonConverter().toJson(simpleChanges);

        if (!simpleChanges.isEmpty()) {
            final OutsourcingCompanyChangeHistory changeHistory = OutsourcingCompanyChangeHistory.builder()
                    .outsourcingCompany(company)
                    .type(OutsourcingCompanyChangeHistoryType.BASIC)
                    .changes(changesJson)
                    .build();
            outsourcingCompanyChangeRepository.save(changeHistory);
        }

        if (request.changeHistories() != null && !request.changeHistories().isEmpty()) {
            for (final ChangeHistoryRequest historyRequest : request.changeHistories()) {
                outsourcingCompanyChangeRepository.findById(historyRequest.id())
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
    public Page<CompanyResponse> getAllOutsourcingCompanies(final OutsourcingCompanyListRequest request,
            final Pageable pageable) {
        return outsourcingCompanyRepository.findAll(request, pageable);
    }

    @Transactional
    public void deleteOutsourcingCompanies(final DeleteOutsourcingCompaniesRequest request) {
        final List<OutsourcingCompany> outsourcingCompanies = outsourcingCompanyRepository
                .findAllById(request.outsourcingCompanyIds());
        if (outsourcingCompanies.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.OUTSOURCING_COMPANY_NOT_FOUND);
        }

        for (final OutsourcingCompany company : outsourcingCompanies) {
            company.markAsDeleted();
        }

        outsourcingCompanyRepository.saveAll(outsourcingCompanies);
    }

    @Transactional(readOnly = true)
    public Workbook downloadExcel(
            final OutsourcingCompanyListRequest request,
            final Sort sort,
            final List<String> fields) {
        final List<CompanyResponse> companyRespons = outsourcingCompanyRepository.findAllWithoutPaging(request, sort)
                .stream()
                .map(CompanyResponse::from)
                .toList();

        return ExcelExportUtils.generateWorkbook(
                companyRespons,
                fields,
                this::getExcelHeaderName,
                this::getExcelCellValue);
    }

    private String getExcelHeaderName(final String field) {
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

    private String getExcelCellValue(final CompanyResponse company, final String field) {
        final var mainContact = company.contacts().stream()
                .filter(CompanyContactResponse::isMain)
                .findFirst()
                .orElse(null);

        return switch (field) {
            case "id" -> String.valueOf(company.id());
            case "name" -> company.name();
            case "businessNumber" -> company.businessNumber();
            case "type" -> company.type();
            case "ceoName" -> company.ceoName();
            case "address" -> {
                final String address = company.address() != null ? company.address() : "";
                final String detailAddress = company.detailAddress() != null ? company.detailAddress() : "";
                final String fullAddress = (address + " " + detailAddress).trim();
                yield fullAddress.isEmpty() ? null : fullAddress;
            }
            case "phoneNumber" -> company.phoneNumber();
            case "landlineNumber" -> company.landlineNumber();
            case "contactName" -> mainContact != null ? mainContact.name() : "";
            case "contactPositionAndDepartment" ->
                mainContact != null ? mainContact.position() + "/" + mainContact.department() : "";
            case "defaultDeductions" -> company.defaultDeductions();
            case "isActive" -> company.isActive() ? "Y" : "N";
            case "createdAtAndUpdatedAt" ->
                DateTimeFormatUtils.formatKoreaLocalDate(company.createdAt()) + "/"
                        + DateTimeFormatUtils.formatKoreaLocalDate(company.updatedAt());
            case "hasFile" -> company.hasFile() ? "Y" : "N";
            case "memo" -> company.memo();
            case "email" -> company.email();
            default -> null;
        };
    }

    @Transactional(readOnly = true)
    public Slice<CompanyChangeHistoryResponse> getOutsourcingCompanyChangeHistories(final Long id,
            final Pageable pageable) {
        final OutsourcingCompany company = outsourcingCompanyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.OUTSOURCING_COMPANY_NOT_FOUND));

        final Slice<OutsourcingCompanyChangeHistory> histories = outsourcingCompanyChangeRepository
                .findAllByOutsourcingCompany(
                        company,
                        pageable);
        return histories.map(CompanyChangeHistoryResponse::from);
    }

    /**
     * 외주업체 변경 이력을 전체 개수와 함께 조회
     * 페이지 네비게이션이 필요한 경우 사용
     */
    @Transactional(readOnly = true)
    public Page<CompanyChangeHistoryResponse> getOutsourcingCompanyChangeHistoriesWithPaging(final Long id,
            final Pageable pageable) {
        final OutsourcingCompany company = outsourcingCompanyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.OUTSOURCING_COMPANY_NOT_FOUND));

        final Page<OutsourcingCompanyChangeHistory> historyPage = outsourcingCompanyChangeRepository
                .findAllByOutsourcingCompanyWithPaging(
                        company,
                        pageable);
        return historyPage.map(CompanyChangeHistoryResponse::from);
    }

    @Transactional(readOnly = true)
    public Slice<CompanyResponse.CompanySimpleResponse> searchByName(final String name, final Pageable pageable) {
        Slice<OutsourcingCompany> companies;

        final String searchName = (name != null && !name.trim().isEmpty()) ? name.trim() : null;
        companies = outsourcingCompanyRepository.findByNameAndKeyword(searchName, pageable);

        return companies.map(company -> CompanyResponse.CompanySimpleResponse.from(company));
    }

    /**
     * 장비 데이터가 존재하는 외주업체 목록을 조회합니다.
     */
    @Transactional(readOnly = true)
    public Page<CompanyResponse.CompanySimpleResponse> getCompaniesWithEquipment(final Pageable pageable) {
        final Page<OutsourcingCompany> page = outsourcingCompanyRepository.findCompaniesWithEquipment(pageable);
        return page.map(CompanyResponse.CompanySimpleResponse::from);
    }
}
