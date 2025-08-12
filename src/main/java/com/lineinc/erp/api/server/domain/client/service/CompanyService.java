package com.lineinc.erp.api.server.domain.client.service;

import com.lineinc.erp.api.server.domain.client.enums.ChangeType;
import com.lineinc.erp.api.server.domain.user.service.UserService;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.shared.util.ExcelExportUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;
import com.lineinc.erp.api.server.domain.client.entity.ClientCompany;
import com.lineinc.erp.api.server.domain.client.entity.ClientCompanyChangeHistory;
import com.lineinc.erp.api.server.domain.client.repository.CompanyChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.client.repository.CompanyRepository;
import com.lineinc.erp.api.server.domain.user.repository.UserRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request.ClientCompanyCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request.ClientCompanyListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request.ClientCompanyUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request.DeleteClientCompaniesRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.response.ClientCompanyChangeHistoryResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.response.ClientCompanyContactResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.response.ClientCompanyDetailResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.response.ClientCompanyResponse;
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
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyContactService contactService;
    private final CompanyFileService fileService;
    private final UserService userService;
    private final Javers javers;
    private final CompanyChangeHistoryRepository companyChangeHistoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createClientCompany(ClientCompanyCreateRequest request) {

        if (companyRepository.existsByBusinessNumber(request.businessNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ValidationMessages.BUSINESS_NUMBER_ALREADY_EXISTS);
        }

        // 1. ClientCompany 객체 먼저 빌드
        ClientCompany clientCompany = ClientCompany.builder()
                .name(request.name())
                .businessNumber(request.businessNumber())
                .ceoName(request.ceoName())
                .address(request.address())
                .detailAddress(request.detailAddress())
                .landlineNumber(request.landlineNumber())
                .phoneNumber(request.phoneNumber())
                .email(request.email())
                .user(request.userId() != null ? userService.getUserByIdOrThrow(request.userId()) : null)
                .paymentMethod(request.paymentMethod())
                .paymentPeriod(request.paymentPeriod())
                .memo(request.memo())
                .isActive(request.isActive())
                .build();

        // 2. 자식 엔티티 생성 + 연관관계 설정
        contactService.createClientCompanyContacts(clientCompany, request.contacts());
        fileService.createClientCompanyFile(clientCompany, request.files());

        // 3. 모든 연관관계 설정 후 save
        companyRepository.save(clientCompany);
    }

    @Transactional(readOnly = true)
    public Page<ClientCompanyResponse> getAllClientCompanies(ClientCompanyListRequest request, Pageable pageable) {
        return companyRepository.findAll(request, pageable);
    }

    @Transactional(readOnly = true)
    public ClientCompany getClientCompanyByIdOrThrow(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.CLIENT_COMPANY_NOT_FOUND));
    }

    @Transactional
    public void deleteClientCompany(Long id) {
        ClientCompany clientCompany = getClientCompanyByIdOrThrow(id);
        clientCompany.markAsDeleted();
    }

    @Transactional
    public void deleteClientCompanies(DeleteClientCompaniesRequest request) {
        List<ClientCompany> clientCompanies = companyRepository.findAllById(request.clientCompanyIds());
        if (clientCompanies.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.CLIENT_COMPANY_NOT_FOUND);
        }

        for (ClientCompany clientCompany : clientCompanies) {
            clientCompany.markAsDeleted();
        }

        companyRepository.saveAll(clientCompanies);
    }

    @Transactional
    public void updateClientCompany(Long id, ClientCompanyUpdateRequest request) {
        ClientCompany clientCompany = getClientCompanyByIdOrThrow(id);

        clientCompany.syncTransientFields();
        ClientCompany oldSnapshot = JaversUtils.createSnapshot(javers, clientCompany, ClientCompany.class);

        clientCompany.updateFrom(request, userRepository);
        companyRepository.save(clientCompany);

        Diff diff = javers.compare(oldSnapshot, clientCompany);
        List<Map<String, String>> simpleChanges = JaversUtils.extractModifiedChanges(javers, diff);
        String changesJson = javers.getJsonConverter().toJson(simpleChanges);

        if (!simpleChanges.isEmpty()) {
            ClientCompanyChangeHistory changeHistory = ClientCompanyChangeHistory.builder()
                    .clientCompany(clientCompany)
                    .type(ChangeType.BASIC)
                    .changes(changesJson)
                    .build();
            companyChangeHistoryRepository.save(changeHistory);
        }

        if (request.changeHistories() != null && !request.changeHistories().isEmpty()) {
            for (ClientCompanyUpdateRequest.ChangeHistoryRequest historyRequest : request.changeHistories()) {
                companyChangeHistoryRepository.findById(historyRequest.id())
                        .filter(history -> history.getClientCompany().getId().equals(clientCompany.getId()))
                        .ifPresent(history -> {
                            history.setMemo(historyRequest.memo());
                        });
            }
        }

        contactService.updateClientCompanyContacts(clientCompany, request.contacts());
        fileService.updateClientCompanyFiles(clientCompany, request.files());
    }


    @Transactional(readOnly = true)
    public Workbook downloadExcel(ClientCompanyListRequest request, Sort sort, List<String> fields) {
        List<ClientCompanyResponse> clientCompanyResponses = companyRepository.findAllWithoutPaging(request, sort)
                .stream()
                .map(ClientCompanyResponse::from)
                .toList();

        return ExcelExportUtils.generateWorkbook(
                clientCompanyResponses,
                fields,
                this::getExcelHeaderName,
                this::getExcelCellValue
        );
    }

    private String getExcelHeaderName(String field) {
        return switch (field) {
            case "id" -> "No.";
            case "businessNumber" -> "사업자등록번호";
            case "name" -> "발주처명";
            case "ceoName" -> "대표자명";
            case "address" -> "본사 주소";
            case "phoneNumber" -> "개인 휴대폰";
            case "landlineNumber" -> "전화번호";
            case "contactName" -> "담당자명";
            case "contactPositionAndDepartment" -> "직급/부서";
            case "contactLandlineNumberAndEmail" -> "담당자 전화번호/이메일";
            case "userName" -> "본사담당자";
            case "isActive" -> "사용여부";
            case "createdAtAndUpdatedAt" -> "등록일/수정일";
            case "hasFile" -> "첨부파일 유무";
            case "memo" -> "비고/메모";
            default -> null;
        };
    }

    private String getExcelCellValue(ClientCompanyResponse company, String field) {
        ClientCompanyContactResponse mainContact = company.contacts().stream()
                .filter(ClientCompanyContactResponse::isMain)
                .findFirst()
                .orElse(null);

        return switch (field) {
            case "id" -> String.valueOf(company.id());
            case "businessNumber" -> company.businessNumber();
            case "name" -> company.name();
            case "ceoName" -> company.ceoName();
            case "address" -> company.address() + " " + company.detailAddress();
            case "phoneNumber" -> company.phoneNumber();
            case "landlineNumber" -> company.landlineNumber();
            case "contactName" -> mainContact != null ? mainContact.name() : "";
            case "contactPositionAndDepartment" ->
                    mainContact != null ? mainContact.position() + " / " + mainContact.department() : "";
            case "contactLandlineNumberAndEmail" ->
                    mainContact != null ? mainContact.landlineNumber() + " / " + mainContact.email() : "";
            case "userName" -> company.user().username();
            case "isActive" -> company.isActive() ? "Y" : "N";
            case "createdAtAndUpdatedAt" ->
                    DateTimeFormatUtils.formatKoreaLocalDate(company.createdAt()) + "/" + DateTimeFormatUtils.formatKoreaLocalDate(company.updatedAt());
            case "hasFile" -> company.hasFile() ? "Y" : "N";
            case "memo" -> company.memo();
            default -> null;
        };
    }

    @Transactional(readOnly = true)
    public ClientCompanyDetailResponse getClientCompanyById(Long id) {
        ClientCompany clientCompany = getClientCompanyByIdOrThrow(id);
        return ClientCompanyDetailResponse.from(clientCompany);
    }

    @Transactional(readOnly = true)
    public Slice<ClientCompanyResponse.ClientCompanySimpleResponse> searchClientCompanyByName(String keyword, Pageable pageable) {
        Slice<ClientCompany> companySlice;

        if (keyword == null || keyword.isBlank()) {
            companySlice = companyRepository.findAllBy(pageable);
        } else {
            companySlice = companyRepository.findByNameContainingIgnoreCase(keyword, pageable);
        }

        return companySlice.map(ClientCompanyResponse.ClientCompanySimpleResponse::from);
    }

    @Transactional(readOnly = true)
    public Slice<ClientCompanyChangeHistoryResponse> getClientCompanyChangeHistories(Long clientCompanyId, Pageable pageable) {
        ClientCompany clientCompany = getClientCompanyByIdOrThrow(clientCompanyId);
        Slice<ClientCompanyChangeHistory> historySlice = companyChangeHistoryRepository.findByClientCompany(clientCompany, pageable);
        return historySlice.map(ClientCompanyChangeHistoryResponse::from);
    }
}
