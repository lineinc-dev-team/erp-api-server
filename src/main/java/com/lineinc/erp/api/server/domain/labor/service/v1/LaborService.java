package com.lineinc.erp.api.server.domain.labor.service.v1;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

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

import com.lineinc.erp.api.server.domain.common.service.S3FileService;
import com.lineinc.erp.api.server.domain.exceldownloadhistory.enums.ExcelDownloadHistoryType;
import com.lineinc.erp.api.server.domain.exceldownloadhistory.service.ExcelDownloadHistoryService;
import com.lineinc.erp.api.server.domain.labor.entity.Labor;
import com.lineinc.erp.api.server.domain.labor.entity.LaborChangeHistory;
import com.lineinc.erp.api.server.domain.labor.entity.LaborFile;
import com.lineinc.erp.api.server.domain.labor.enums.LaborChangeType;
import com.lineinc.erp.api.server.domain.labor.enums.LaborType;
import com.lineinc.erp.api.server.domain.labor.repository.LaborChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.labor.repository.LaborRepository;
import com.lineinc.erp.api.server.domain.organization.entity.Grade;
import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcingcompany.service.v1.OutsourcingCompanyService;
import com.lineinc.erp.api.server.domain.user.service.v1.UserService;
import com.lineinc.erp.api.server.infrastructure.config.security.CustomUserDetails;
import com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.request.DeleteLaborsRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.request.LaborCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.request.LaborFileCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.request.LaborListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.request.LaborUpdateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.response.LaborChangeHistoryResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.response.LaborDetailResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.response.LaborListResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.response.LaborNameResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.response.TypeDescriptionResponse;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.shared.util.ExcelExportUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;
import com.lineinc.erp.api.server.shared.util.PrivacyMaskingUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class LaborService {

    private final LaborRepository laborRepository;
    private final LaborChangeHistoryRepository laborChangeHistoryRepository;
    private final OutsourcingCompanyService outsourcingCompanyService;
    private final LaborFileService laborFileService;
    private final Javers javers;
    private final UserService userService;
    private final S3FileService s3FileService;
    private final ExcelDownloadHistoryService excelDownloadHistoryService;

    /**
     * 노무 등록
     */
    public void createLabor(final LaborCreateRequest request, final Long userId) {
        // 주민등록번호 중복 체크 (* 포함시 제외)
        if (request.residentNumber() != null && !request.residentNumber().contains("*")
                && laborRepository.existsByResidentNumber(request.residentNumber())) {
            throw new IllegalArgumentException(ValidationMessages.LABOR_ALREADY_EXISTS);
        }

        // 외주업체 조회 및 본사 인력 여부 판단
        OutsourcingCompany outsourcingCompany = null;
        Boolean isHeadOffice = false;

        if (request.outsourcingCompanyId() != null && request.outsourcingCompanyId() != 0) {
            outsourcingCompany = outsourcingCompanyService
                    .getOutsourcingCompanyByIdOrThrow(request.outsourcingCompanyId());
        } else if (request.outsourcingCompanyId() != null && request.outsourcingCompanyId() == 0) {
            // outsourcingCompanyId가 0인 경우 본사 인력으로 처리
            isHeadOffice = true;
        }

        final Labor labor = Labor.builder()
                .type(request.type())
                .typeDescription(request.typeDescription())
                .workType(request.workType())
                .workTypeDescription(request.workTypeDescription())
                .isHeadOffice(isHeadOffice)
                .mainWork(request.mainWork())
                .dailyWage(request.dailyWage())
                .bankName(request.bankName())
                .accountNumber(request.accountNumber())
                .accountHolder(request.accountHolder())
                .outsourcingCompany(outsourcingCompany)
                .name(request.name())
                .residentNumber(request.residentNumber())
                .address(request.address())
                .detailAddress(request.detailAddress())
                .phoneNumber(request.phoneNumber())
                .memo(request.memo())
                .grade(request.gradeId() != null ? Grade.builder().id(request.gradeId()).build() : null)
                .build();

        // 첨부파일 처리
        if (request.files() != null && !request.files().isEmpty()) {
            final List<LaborFile> laborFiles = request.files().stream()
                    .map(fileRequest -> createLaborFile(labor, fileRequest))
                    .collect(Collectors.toList());

            // Labor 엔티티에 파일 목록 설정 (양방향 관계)
            labor.setFiles(laborFiles);
        }

        labor.syncTransientFields();
        laborRepository.save(labor);

        final LaborChangeHistory changeHistory = LaborChangeHistory.builder()
                .labor(labor)
                .description(ValidationMessages.INITIAL_CREATION)
                .user(userService.getUserByIdOrThrow(userId))
                .build();
        laborChangeHistoryRepository.save(changeHistory);
    }

    /**
     * 노무 파일 생성
     */
    private LaborFile createLaborFile(final Labor labor, final LaborFileCreateRequest fileRequest) {
        return LaborFile.builder()
                .labor(labor)
                .name(fileRequest.name())
                .fileUrl(fileRequest.fileUrl())
                .originalFileName(fileRequest.originalFileName())
                .type(fileRequest.type())
                .memo(fileRequest.memo())
                .build();
    }

    /**
     * ETC 노무 구분 설명 목록 조회
     */
    public Slice<TypeDescriptionResponse> getEtcTypeDescriptions(final String keyword, final Pageable pageable) {
        Slice<Object[]> resultSlice;

        if (keyword == null || keyword.isBlank()) {
            resultSlice = laborRepository.findAllDistinctTypeDescriptions(LaborType.ETC, pageable);
        } else {
            resultSlice = laborRepository.findDistinctTypeDescriptionsByKeyword(LaborType.ETC, keyword, pageable);
        }

        return resultSlice.map(result -> new TypeDescriptionResponse((Long) result[1], (String) result[0]));
    }

    /**
     * 인력명 키워드 검색 (노무인력 유형별 필터링 포함)
     */
    public Slice<LaborNameResponse> getLaborNames(final String keyword, final List<LaborType> types,
            final Long outsourcingCompanyId, final Boolean isHeadOffice, final Pageable pageable) {
        final String searchKeyword = (keyword == null || keyword.isBlank()) ? null : keyword;
        final Slice<Labor> laborSlice = laborRepository.findAllByNameAndType(searchKeyword, types, outsourcingCompanyId,
                isHeadOffice, pageable);
        return laborSlice.map(LaborNameResponse::from);
    }

    /**
     * 여러 인력정보 삭제
     */
    public void deleteLaborsByIds(final DeleteLaborsRequest request) {
        final List<Labor> labors = laborRepository.findAllById(request.laborIds());
        if (request.laborIds().size() != labors.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.LABOR_NOT_FOUND);
        }

        for (final Labor labor : labors) {
            labor.markAsDeleted();
        }
        laborRepository.saveAll(labors);
    }

    /**
     * 인력정보 엑셀 다운로드
     */
    @Transactional(readOnly = true)
    public Workbook downloadExcel(final CustomUserDetails user, final LaborListRequest request, final Sort sort,
            final List<String> fields) {
        final List<LaborListResponse> laborResponses = laborRepository.findAllWithoutPaging(request, sort);

        final Workbook workbook = ExcelExportUtils.generateWorkbook(
                laborResponses,
                fields,
                this::getExcelHeaderName,
                this::getExcelCellValue);

        final String fileUrl = s3FileService.uploadExcelToS3(workbook,
                ExcelDownloadHistoryType.LABOR_MANAGEMENT.name());

        excelDownloadHistoryService.recordDownload(
                ExcelDownloadHistoryType.LABOR_MANAGEMENT,
                userService.getUserByIdOrThrow(user.getUserId()),
                fileUrl);

        return workbook;
    }

    /**
     * 특정 인력정보 조회
     */
    @Transactional(readOnly = true)
    public LaborDetailResponse getLaborById(final Long id) {
        final Labor labor = laborRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.LABOR_NOT_FOUND));
        return LaborDetailResponse.from(labor);
    }

    /**
     * 인력정보 수정
     */
    @Transactional
    public void updateLabor(final Long id, final LaborUpdateRequest request, final Long userId) {
        final Labor labor = getLaborByIdOrThrow(id);

        // 주민등록번호 중복 체크 (현재 인력 제외, * 포함시 제외)
        if (request.residentNumber() != null && !request.residentNumber().contains("*")
                && laborRepository.existsByResidentNumberExcludingId(request.residentNumber(), id)) {
            throw new IllegalArgumentException(ValidationMessages.LABOR_ALREADY_EXISTS);
        }

        // 외주업체 조회 및 본사 인력 여부 판단
        OutsourcingCompany outsourcingCompany = null;
        Boolean isHeadOffice = false;

        if (request.outsourcingCompanyId() != null && request.outsourcingCompanyId() != 0) {
            outsourcingCompany = outsourcingCompanyService
                    .getOutsourcingCompanyByIdOrThrow(request.outsourcingCompanyId());
        } else if (request.outsourcingCompanyId() != null && request.outsourcingCompanyId() == 0) {
            // outsourcingCompanyId가 0인 경우 본사 인력으로 처리
            isHeadOffice = true;
            outsourcingCompany = null;
        }

        // 변경 전 상태 저장 (Javers 스냅샷)
        labor.syncTransientFields();
        final Labor oldSnapshot = JaversUtils.createSnapshot(javers, labor, Labor.class);

        // 기본 정보 업데이트
        labor.updateFrom(request, outsourcingCompany, isHeadOffice);

        labor.syncTransientFields();
        laborRepository.save(labor);

        // Javers를 사용하여 변경사항 추적
        final Diff diff = javers.compare(oldSnapshot, labor);
        final List<Map<String, String>> simpleChanges = JaversUtils.extractModifiedChanges(javers, diff);
        final String changesJson = javers.getJsonConverter().toJson(simpleChanges);

        // 변경사항이 있을 때만 수정이력 생성
        if (!simpleChanges.isEmpty()) {
            final LaborChangeHistory changeHistory = LaborChangeHistory.builder()
                    .labor(labor)
                    .type(LaborChangeType.BASIC)
                    .changes(changesJson)
                    .user(userService.getUserByIdOrThrow(userId))
                    .build();
            laborChangeHistoryRepository.save(changeHistory);
        }

        // 첨부파일 처리
        laborFileService.updateLaborFiles(labor, request.files(), userId);

        // 변경이력 메모 수정 처리
        if (request.changeHistories() != null && !request.changeHistories().isEmpty()) {
            for (final LaborUpdateRequest.ChangeHistoryRequest historyRequest : request.changeHistories()) {
                laborChangeHistoryRepository.findById(historyRequest.id())
                        .filter(history -> history.getLabor().getId().equals(labor.getId()))
                        .ifPresent(history -> {
                            history.setMemo(historyRequest.memo());
                        });
            }
        }
    }

    /**
     * 엑셀 헤더명 반환
     */
    private String getExcelHeaderName(final String field) {
        return switch (field) {
            case "id" -> "No.";
            case "type" -> "구분";
            case "name" -> "이름";
            case "residentNumber" -> "주민번호";
            case "outsourcingCompanyName" -> "소속업체";
            case "workType" -> "공종";
            case "mainWork" -> "주 작업";
            case "phoneNumber" -> "연락처";
            case "dailyWage" -> "기준일당";
            case "accountNumber" -> "계좌번호";
            case "hireDate" -> "입사일";
            case "resignationDate" -> "퇴사일";
            case "tenureMonths" -> "근속기간";
            case "isSeverancePayEligible" -> "퇴직금 발생";
            case "hasBankbook" -> "통장 사본";
            case "hasIdCard" -> "신분증 사본";
            case "hasSignatureImage" -> "서명 이미지";
            case "hasFile" -> "기타첨부";
            default -> null;
        };
    }

    /**
     * 엑셀 셀 값 반환
     */
    private String getExcelCellValue(final LaborListResponse labor, final String field) {
        return switch (field) {
            case "id" -> labor.id() != null ? labor.id().toString() : "";
            case "type" -> labor.type() != null ? labor.type() : "";
            case "name" -> labor.name() != null ? labor.name() : "";
            case "residentNumber" ->
                labor.residentNumber() != null ? PrivacyMaskingUtils.maskResidentNumber(labor.residentNumber()) : "";
            case "outsourcingCompanyName" -> {
                if (labor.isHeadOffice() != null && labor.isHeadOffice()) {
                    yield AppConstants.LINE_INC_NAME;
                } else {
                    yield labor.outsourcingCompany() != null ? labor.outsourcingCompany().name() : "";
                }
            }
            case "workType" -> labor.workType() != null ? labor.workType() : "";
            case "mainWork" -> labor.mainWork() != null ? labor.mainWork() : "";
            case "phoneNumber" -> labor.phoneNumber() != null ? labor.phoneNumber() : "";
            case "dailyWage" ->
                labor.dailyWage() != null ? NumberFormat.getNumberInstance(Locale.KOREA).format(labor.dailyWage()) : "";
            case "accountNumber" -> {
                final String bankName = labor.bankName() != null ? labor.bankName() : "";
                final String accountNumber = labor.accountNumber() != null ? labor.accountNumber() : "";
                if (!bankName.isEmpty() && !accountNumber.isEmpty()) {
                    yield bankName + " " + accountNumber;
                } else if (!bankName.isEmpty()) {
                    yield bankName;
                } else if (!accountNumber.isEmpty()) {
                    yield accountNumber;
                } else {
                    yield "";
                }
            }
            case "hireDate" -> labor.hireDate() != null
                    ? DateTimeFormatUtils.formatKoreaLocalDate(labor.hireDate())
                    : "";
            case "resignationDate" -> labor.resignationDate() != null
                    ? DateTimeFormatUtils.formatKoreaLocalDate(labor.resignationDate())
                    : "";
            case "tenureMonths" -> labor.tenureMonths() != null ? labor.tenureMonths() + "개월" : "";
            case "isSeverancePayEligible" ->
                labor.isSeverancePayEligible() != null ? (labor.isSeverancePayEligible() ? "Y" : "N") : "";
            case "hasBankbook" -> labor.hasBankbook() != null ? (labor.hasBankbook() ? "Y" : "N") : "";
            case "hasIdCard" -> labor.hasIdCard() != null ? (labor.hasIdCard() ? "Y" : "N") : "";
            case "hasSignatureImage" ->
                labor.hasSignatureImage() != null ? (labor.hasSignatureImage() ? "Y" : "N") : "";
            case "hasFile" -> labor.hasFile() != null ? (labor.hasFile() ? "Y" : "N") : "";
            default -> "";
        };
    }

    /**
     * ID로 노무 조회 (삭제되지 않은 것만)
     */
    @Transactional(readOnly = true)
    public Labor getLaborByIdOrThrow(final Long id) {
        // -1인 경우 null 반환 (연관관계 끊기)
        if (id != null && id == -1L) {
            return null;
        }
        return laborRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new IllegalArgumentException(ValidationMessages.LABOR_NOT_FOUND));
    }

    /**
     * 인력정보 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<LaborListResponse> getLaborList(final LaborListRequest request, final Pageable pageable) {
        return laborRepository.findAll(request, pageable);
    }

    /**
     * 인력정보 변경 이력 조회
     */
    @Transactional(readOnly = true)
    public Slice<LaborChangeHistoryResponse> getLaborChangeHistories(final Long id, final Pageable pageable,
            final Long userId) {
        // 인력정보 존재 확인
        final Labor labor = getLaborByIdOrThrow(id);

        // LaborChangeHistory를 labor 기준으로 슬라이스 조회
        final Slice<LaborChangeHistory> historySlice = laborChangeHistoryRepository.findByLabor(labor, pageable);

        // 엔티티를 DTO로 변환하여 슬라이스 반환
        return historySlice.map(history -> LaborChangeHistoryResponse.from(history, userId));
    }

    /**
     * 인력정보 변경 이력을 전체 개수와 함께 조회
     * 페이지 네비게이션이 필요한 경우 사용
     */
    @Transactional(readOnly = true)
    public Page<LaborChangeHistoryResponse> getLaborChangeHistoriesWithPaging(final Long id, final Pageable pageable,
            final Long userId) {
        // 인력정보 존재 확인
        final Labor labor = getLaborByIdOrThrow(id);

        // LaborChangeHistory를 labor 기준으로 페이지 조회
        final Page<LaborChangeHistory> historyPage = laborChangeHistoryRepository.findByLaborWithPaging(labor,
                pageable);

        // 엔티티를 DTO로 변환하여 페이지 반환
        return historyPage.map(history -> LaborChangeHistoryResponse.from(history, userId));
    }
}
