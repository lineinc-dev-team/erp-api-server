package com.lineinc.erp.api.server.domain.client.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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

import com.lineinc.erp.api.server.domain.client.entity.ClientCompany;
import com.lineinc.erp.api.server.domain.client.entity.ClientCompanyChangeHistory;
import com.lineinc.erp.api.server.domain.client.enums.ClientCompanyChangeHistoryChangeType;
import com.lineinc.erp.api.server.domain.client.repository.CompanyChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.client.repository.CompanyRepository;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.service.UserService;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request.ClientCompanyCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request.ClientCompanyDeleteRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request.ClientCompanyListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request.ClientCompanyUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.response.ClientCompanyChangeHistoryResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.response.ClientCompanyDetailResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.response.ClientCompanyResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.response.ClientCompanySimpleResponse;
import com.lineinc.erp.api.server.shared.dto.request.ChangeHistoryRequest;
import com.lineinc.erp.api.server.shared.excel.ClientCompanyExcelConfig;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.ExcelExportUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyContactService contactService;
    private final CompanyFileService fileService;
    private final UserService userService;
    private final Javers javers;
    private final CompanyChangeHistoryRepository companyChangeHistoryRepository;

    @Transactional
    public void createClientCompany(final ClientCompanyCreateRequest request) {
        // 1. 사업자등록번호 중복 확인
        validateBusinessNumberNotExists(request.businessNumber());

        // 2. 담당자 정보 조회 (userId가 있는 경우에만)
        final User user = Optional.ofNullable(request.userId())
                .map(userService::getUserByIdOrThrow)
                .orElse(null);

        // 3. ClientCompany 엔티티 생성
        final ClientCompany clientCompany = ClientCompany.createFrom(request, user);

        // 4. 연관된 자식 엔티티들 생성 및 연관관계 설정
        contactService.createClientCompanyContacts(clientCompany, request.contacts());
        fileService.createClientCompanyFile(clientCompany, request.files());

        // 5. 최종 저장
        companyRepository.save(clientCompany);
    }

    @Transactional(readOnly = true)
    public Page<ClientCompanyResponse> getAllClientCompanies(
            final ClientCompanyListRequest request,
            final Pageable pageable) {
        return companyRepository.findAll(request, pageable);
    }

    @Transactional(readOnly = true)
    public ClientCompany getClientCompanyByIdOrThrow(final Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        ValidationMessages.CLIENT_COMPANY_NOT_FOUND));
    }

    @Transactional
    public void deleteClientCompanies(final ClientCompanyDeleteRequest request) {
        final List<ClientCompany> clientCompanies = companyRepository.findAllById(request.clientCompanyIds());
        clientCompanies.forEach(ClientCompany::markAsDeleted);
        companyRepository.saveAll(clientCompanies);
    }

    @Transactional
    public void updateClientCompany(final Long id, final ClientCompanyUpdateRequest request) {
        final ClientCompany clientCompany = getClientCompanyByIdOrThrow(id);

        // 1. 사업자등록번호 중복 확인 (기존 값과 다를 때만)
        if (!clientCompany.getBusinessNumber().equals(request.businessNumber())) {
            validateBusinessNumberNotExists(request.businessNumber());
        }

        clientCompany.syncTransientFields();
        final ClientCompany oldSnapshot = JaversUtils.createSnapshot(javers, clientCompany, ClientCompany.class);
        clientCompany.updateFrom(request, userService.getUserByIdOrThrow(request.userId()));
        companyRepository.save(clientCompany);

        final Diff diff = javers.compare(oldSnapshot, clientCompany);
        final List<Map<String, String>> simpleChanges = JaversUtils.extractModifiedChanges(javers, diff);
        final String changesJson = javers.getJsonConverter().toJson(simpleChanges);

        if (!simpleChanges.isEmpty()) {
            final ClientCompanyChangeHistory changeHistory = ClientCompanyChangeHistory.builder()
                    .clientCompany(clientCompany)
                    .type(ClientCompanyChangeHistoryChangeType.BASIC)
                    .changes(changesJson)
                    .build();
            companyChangeHistoryRepository.save(changeHistory);
        }

        if (request.changeHistories() != null && !request.changeHistories().isEmpty()) {
            for (final ChangeHistoryRequest historyRequest : request.changeHistories()) {
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
    public Workbook downloadExcel(final ClientCompanyListRequest request, final Sort sort, final List<String> fields) {
        final List<ClientCompanyResponse> clientCompanyResponses = companyRepository.findAll(request,
                Pageable.unpaged(sort))
                .getContent();
        return ExcelExportUtils.generateWorkbook(
                clientCompanyResponses,
                fields,
                ClientCompanyExcelConfig::getHeaderName,
                ClientCompanyExcelConfig::getCellValue);
    }

    @Transactional(readOnly = true)
    public ClientCompanyDetailResponse getClientCompanyById(final Long id) {
        final ClientCompany clientCompany = getClientCompanyByIdOrThrow(id);
        return ClientCompanyDetailResponse.from(clientCompany);
    }

    @Transactional(readOnly = true)
    public Slice<ClientCompanySimpleResponse> searchClientCompanyByName(
            final String keyword,
            final Pageable pageable) {
        return companyRepository.findByKeyword(keyword, pageable)
                .map(ClientCompanySimpleResponse::from);
    }

    @Transactional(readOnly = true)
    public Slice<ClientCompanyChangeHistoryResponse> getClientCompanyChangeHistories(final Long clientCompanyId,
            final Pageable pageable) {
        final ClientCompany clientCompany = getClientCompanyByIdOrThrow(clientCompanyId);
        final Slice<ClientCompanyChangeHistory> historySlice = companyChangeHistoryRepository
                .findByClientCompany(clientCompany, pageable);
        return historySlice.map(ClientCompanyChangeHistoryResponse::from);
    }

    /**
     * 발주처 변경 이력을 전체 개수와 함께 조회
     * 페이지 네비게이션이 필요한 경우 사용
     */
    @Transactional(readOnly = true)
    public Page<ClientCompanyChangeHistoryResponse> getClientCompanyChangeHistoriesWithPaging(
            final Long clientCompanyId,
            final Pageable pageable) {
        final ClientCompany clientCompany = getClientCompanyByIdOrThrow(clientCompanyId);
        final Page<ClientCompanyChangeHistory> historyPage = companyChangeHistoryRepository
                .findByClientCompanyWithPaging(clientCompany, pageable);
        return historyPage.map(ClientCompanyChangeHistoryResponse::from);
    }

    // 사업자등록번호 중복 확인
    private void validateBusinessNumberNotExists(final String businessNumber) {
        if (companyRepository.existsByBusinessNumber(businessNumber)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    ValidationMessages.BUSINESS_NUMBER_ALREADY_EXISTS);
        }
    }
}
