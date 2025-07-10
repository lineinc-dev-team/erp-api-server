package com.lineinc.erp.api.server.application.client;

import com.lineinc.erp.api.server.common.util.ExcelExportUtils;
import com.lineinc.erp.api.server.domain.client.entity.ClientCompany;
import com.lineinc.erp.api.server.domain.client.repository.ClientCompanyRepository;
import com.lineinc.erp.api.server.presentation.v1.client.dto.request.ClientCompanyCreateRequest;
import com.lineinc.erp.api.server.presentation.v1.client.dto.request.ClientCompanyListRequest;
import com.lineinc.erp.api.server.presentation.v1.client.dto.request.ClientCompanyUpdateRequest;
import com.lineinc.erp.api.server.presentation.v1.client.dto.response.ClientCompanyResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientCompanyService {

    private final ClientCompanyRepository clientCompanyRepository;
    private final ClientCompanyContactService contactService;
    private final ClientCompanyFileService fileService;

    @Transactional
    public void createClientCompany(ClientCompanyCreateRequest request) {

//        // 1. ClientCompany 객체 먼저 빌드
        ClientCompany clientCompany = ClientCompany.builder()
                .name(request.name())
                .businessNumber(request.businessNumber())
                .ceoName(request.ceoName())
                .address(request.address())
                .landlineNumber(request.landlineNumber())
                .phoneNumber(request.phoneNumber())
                .email(request.email())
                .paymentMethod(request.paymentMethod())
                .paymentPeriod(request.paymentPeriod())
                .memo(request.memo())
                .isActive(request.isActive())
                .build();

        // 2. 자식 엔티티 생성 + 연관관계 설정
        contactService.createClientCompanyContacts(clientCompany, request.contacts());
        fileService.createClientCompanyFile(clientCompany, request.files());

        // 3. 모든 연관관계 설정 후 save
        clientCompanyRepository.save(clientCompany);
    }

    @Transactional(readOnly = true)
    public Page<ClientCompanyResponse> getAllClientCompanies(ClientCompanyListRequest request, Pageable pageable) {
        return clientCompanyRepository.findAll(request, pageable);
    }

    @Transactional(readOnly = true)
    public ClientCompany getClientCompanyByIdOrThrow(Long id) {
        return clientCompanyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    public void deleteClientCompany(Long id) {
        ClientCompany clientCompany = getClientCompanyByIdOrThrow(id);
        clientCompany.markAsDeleted();
    }

    @Transactional
    public void deleteClientCompanies(List<Long> ids) {
        for (Long id : ids) {
            deleteClientCompany(id);
        }
    }

    @Transactional
    public void updateClientCompany(Long id, ClientCompanyUpdateRequest request) {
        ClientCompany clientCompany = getClientCompanyByIdOrThrow(id);

        // 기본 필드 업데이트
        clientCompany.updateFrom(request);

        // 담당자 정보 갱신
        contactService.updateClientCompanyContacts(clientCompany, request.contacts());

        // 첨부파일 정보 갱신
        fileService.updateClientCompanyFiles(clientCompany, request.files());
    }

    @Transactional(readOnly = true)
    public Workbook downloadExcel(ClientCompanyListRequest request, Sort sort, List<String> fields) {
        List<ClientCompanyResponse> clientCompanyResponses = clientCompanyRepository.findAllWithoutPaging(request, sort)
                .stream()
                .map(ClientCompanyResponse::from)
                .toList();

        return ExcelExportUtils.generateWorkbook(
                "발주처 목록",
                clientCompanyResponses,
                fields,
                this::getExcelHeaderName,
                this::getExcelCellValue
        );
    }

    private String getExcelHeaderName(String field) {
        return switch (field) {
            case "id" -> "No.";
            case "businessNumber" -> "사업자번호";
            case "name" -> "이름";
            case "ceoName" -> "대표자명";
            case "address" -> "주소";
            default -> null;
        };
    }

    private String getExcelCellValue(ClientCompanyResponse company, String field) {
        return switch (field) {
            case "id" -> String.valueOf(company.id());
            case "businessNumber" -> company.businessNumber();
            case "name" -> company.name();
            case "ceoName" -> company.ceoName();
            case "address" -> company.address();
            default -> null;
        };
    }
}