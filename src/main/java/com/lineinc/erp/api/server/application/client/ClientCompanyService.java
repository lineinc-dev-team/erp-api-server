package com.lineinc.erp.api.server.application.client;

import com.lineinc.erp.api.server.application.user.UserService;
import com.lineinc.erp.api.server.common.constant.ValidationMessages;
import com.lineinc.erp.api.server.common.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.common.util.ExcelExportUtils;
import com.lineinc.erp.api.server.domain.client.entity.ClientCompany;
import com.lineinc.erp.api.server.domain.client.entity.ClientCompanyChangeHistory;
import com.lineinc.erp.api.server.domain.client.enums.PaymentMethod;
import com.lineinc.erp.api.server.domain.client.repository.ClientCompanyChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.client.repository.ClientCompanyRepository;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.entity.UserChangeHistory;
import com.lineinc.erp.api.server.domain.user.repository.UserRepository;
import com.lineinc.erp.api.server.presentation.v1.client.dto.request.ClientCompanyCreateRequest;
import com.lineinc.erp.api.server.presentation.v1.client.dto.request.ClientCompanyListRequest;
import com.lineinc.erp.api.server.presentation.v1.client.dto.request.ClientCompanyUpdateRequest;
import com.lineinc.erp.api.server.presentation.v1.client.dto.request.DeleteClientCompaniesRequest;
import com.lineinc.erp.api.server.presentation.v1.client.dto.response.ClientCompanyChangeHistoryResponse;
import com.lineinc.erp.api.server.presentation.v1.client.dto.response.ClientCompanyDetailResponse;
import com.lineinc.erp.api.server.presentation.v1.client.dto.response.ClientCompanyResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ValueChange;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
    private final UserService userService;
    private final Javers javers;
    private final ClientCompanyChangeHistoryRepository clientCompanyChangeHistoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createClientCompany(ClientCompanyCreateRequest request) {

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
        clientCompanyRepository.save(clientCompany);
    }

    @Transactional(readOnly = true)
    public Page<ClientCompanyResponse> getAllClientCompanies(ClientCompanyListRequest request, Pageable pageable) {
        return clientCompanyRepository.findAll(request, pageable);
    }

    @Transactional(readOnly = true)
    public ClientCompany getClientCompanyByIdOrThrow(Long id) {
        return clientCompanyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.CLIENT_COMPANY_NOT_FOUND));
    }

    @Transactional
    public void deleteClientCompany(Long id) {
        ClientCompany clientCompany = getClientCompanyByIdOrThrow(id);
        clientCompany.markAsDeleted();
    }

    @Transactional
    public void deleteClientCompanies(DeleteClientCompaniesRequest request) {
        List<ClientCompany> clientCompanies = clientCompanyRepository.findAllById(request.clientCompanyIds());
        if (clientCompanies.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.CLIENT_COMPANY_NOT_FOUND);
        }

        for (ClientCompany clientCompany : clientCompanies) {
            clientCompany.markAsDeleted();
        }

        clientCompanyRepository.saveAll(clientCompanies);
    }

    @Transactional
    public void updateClientCompany(Long id, ClientCompanyUpdateRequest request) {
        ClientCompany clientCompany = getClientCompanyByIdOrThrow(id);

        // 변경 이력 추적 및 반영
        ClientCompany.ClientCompanyUpdateResult result = clientCompany.updateFromWithHistory(request, userRepository);

        Diff diff = javers.compare(result.before(), result.after());

        // 변경된 필드가 있을 경우 변경 이력 저장
        String changeDetail = buildClientCompanyChangeDetail(diff);
        if (!changeDetail.isBlank()) {
            ClientCompanyChangeHistory changeHistory = ClientCompanyChangeHistory.builder()
                    .clientCompany(clientCompany)
                    .changeDetail(changeDetail)
                    .build();
            clientCompanyChangeHistoryRepository.save(changeHistory);
        }

        // 담당자 정보 갱신
        contactService.updateClientCompanyContacts(clientCompany, request.contacts());

        // 첨부파일 정보 갱신
        fileService.updateClientCompanyFiles(clientCompany, request.files());
    }

    private String buildClientCompanyChangeDetail(Diff diff) {
        StringBuilder changeDetailBuilder = new StringBuilder();
        diff.getChanges().forEach(change -> {
            if (change instanceof ValueChange valueChange) {
                String propertyName = valueChange.getPropertyName();
                String label = switch (propertyName) {
                    case "name" -> "발주처명";
                    case "businessNumber" -> "사업자등록번호";
                    case "ceoName" -> "대표자명";
                    case "address" -> "본사 주소";
                    case "detailAddress" -> "상세 주소";
                    case "landlineNumber" -> "전화번호";
                    case "phoneNumber" -> "휴대폰번호";
                    case "email" -> "이메일";
                    case "paymentMethod" -> "결제 방식";
                    case "paymentPeriod" -> "결제 주기";
                    case "memo" -> "비고";
                    case "isActive" -> "사용 여부";
                    case "userName" -> "본사담당자명";
                    default -> null;
                };
                if (label != null) {
                    Object left = valueChange.getLeft();
                    Object right = valueChange.getRight();
                    String leftStr = left == null ? "" : left.toString();
                    String rightStr = right == null ? "" : right.toString();
                    if ("isActive".equals(propertyName)) {
                        leftStr = "true".equals(leftStr) ? "활성" : "비활성";
                        rightStr = "true".equals(rightStr) ? "활성" : "비활성";
                    } else if ("paymentMethod".equals(propertyName)) {
                        leftStr = left instanceof PaymentMethod pm ? pm.getDisplayName() : leftStr;
                        rightStr = right instanceof PaymentMethod pm ? pm.getDisplayName() : rightStr;
                    }
                    if (rightStr.isBlank()) return;
                    changeDetailBuilder.append(label)
                            .append(" : ")
                            .append(leftStr)
                            .append(" → ")
                            .append(rightStr)
                            .append("\n");
                }
            }
        });
        return changeDetailBuilder.toString();
    }

    @Transactional(readOnly = true)
    public Workbook downloadExcel(ClientCompanyListRequest request, Sort sort, List<String> fields) {
        List<ClientCompanyResponse> clientCompanyResponses = clientCompanyRepository.findAllWithoutPaging(request, sort)
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
            case "businessNumber" -> "사업자번호";
            case "name" -> "이름";
            case "ceoName" -> "대표자명";
            case "address" -> "본사 주소";
            case "phoneNumber" -> "전화번호";
            case "isActive" -> "사용여부";
            case "createdAt" -> "등록일";
            case "updatedAt" -> "수정일";
            case "hasFile" -> "첨부파일 유무";
            case "memo" -> "비고/메모";
            default -> null;
        };
    }

    private String getExcelCellValue(ClientCompanyResponse company, String field) {
        return switch (field) {
            case "id" -> String.valueOf(company.id());
            case "businessNumber" -> company.businessNumber();
            case "name" -> company.name();
            case "ceoName" -> company.ceoName();
            case "address" -> company.address() + " " + company.detailAddress();
            case "phoneNumber" -> company.phoneNumber();
            case "isActive" -> String.valueOf(company.isActive());
            case "createdAt" -> DateTimeFormatUtils.formatKoreaLocalDate(company.createdAt());
            case "updatedAt" -> DateTimeFormatUtils.formatKoreaLocalDate(company.updatedAt());
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
            companySlice = clientCompanyRepository.findAllBy(pageable);
        } else {
            companySlice = clientCompanyRepository.findByNameContainingIgnoreCase(keyword, pageable);
        }

        return companySlice.map(ClientCompanyResponse.ClientCompanySimpleResponse::from);
    }

    @Transactional(readOnly = true)
    public Slice<ClientCompanyChangeHistoryResponse> getClientCompanyChangeHistories(Long clientCompanyId, Pageable pageable) {
        ClientCompany clientCompany = getClientCompanyByIdOrThrow(clientCompanyId);
        Slice<ClientCompanyChangeHistory> historySlice = clientCompanyChangeHistoryRepository.findByClientCompany(clientCompany, pageable);
        return historySlice.map(ClientCompanyChangeHistoryResponse::from);
    }
}

