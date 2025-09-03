package com.lineinc.erp.api.server.domain.labormanagement.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.labormanagement.entity.Labor;
import com.lineinc.erp.api.server.domain.labormanagement.entity.LaborFile;
import com.lineinc.erp.api.server.domain.labormanagement.entity.LaborChangeHistory;
import com.lineinc.erp.api.server.domain.labormanagement.enums.LaborChangeType;
import com.lineinc.erp.api.server.domain.labormanagement.repository.LaborRepository;
import com.lineinc.erp.api.server.domain.labormanagement.repository.LaborChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcing.service.OutsourcingCompanyService;
import com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.request.LaborCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.request.LaborFileCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.request.LaborListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.request.DeleteLaborsRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.response.LaborListResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.response.LaborNameResponse;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.shared.util.ExcelExportUtils;
import com.lineinc.erp.api.server.shared.util.PrivacyMaskingUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;
import org.javers.core.Javers;
import org.javers.core.diff.Diff;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Sort;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.lineinc.erp.api.server.domain.labormanagement.enums.LaborType;
import com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.response.TypeDescriptionResponse;

import org.springframework.data.domain.Slice;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.response.LaborChangeHistoryResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.response.LaborDetailResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.request.LaborUpdateRequest;

@Service
@RequiredArgsConstructor
@Transactional
public class LaborService {

    private final LaborRepository laborRepository;
    private final LaborChangeHistoryRepository laborChangeHistoryRepository;
    private final OutsourcingCompanyService outsourcingCompanyService;
    private final LaborFileService laborFileService;
    private final Javers javers;

    /**
     * 노무 등록
     */
    public void createLabor(LaborCreateRequest request) {
        // 주민등록번호 중복 체크
        if (laborRepository.existsByResidentNumber(request.residentNumber())) {
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

        Labor labor = Labor.builder()
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
                .hireDate(DateTimeFormatUtils.toOffsetDateTime(request.hireDate()))
                .resignationDate(DateTimeFormatUtils.toOffsetDateTime(request.resignationDate()))
                .outsourcingCompany(outsourcingCompany)
                .name(request.name())
                .residentNumber(request.residentNumber())
                .address(request.address())
                .detailAddress(request.detailAddress())
                .phoneNumber(request.phoneNumber())
                .memo(request.memo())
                .build();

        // 첨부파일 처리
        if (request.files() != null && !request.files().isEmpty()) {
            List<LaborFile> laborFiles = request.files().stream()
                    .map(fileRequest -> createLaborFile(labor, fileRequest))
                    .collect(Collectors.toList());

            // Labor 엔티티에 파일 목록 설정 (양방향 관계)
            labor.setFiles(laborFiles);
        }

        labor.syncTransientFields();
        laborRepository.save(labor);
    }

    /**
     * 노무 파일 생성
     */
    private LaborFile createLaborFile(Labor labor, LaborFileCreateRequest fileRequest) {
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
    public Slice<TypeDescriptionResponse> getEtcTypeDescriptions(String keyword, Pageable pageable) {
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
    public Slice<LaborNameResponse> getLaborNames(String keyword, LaborType type, Pageable pageable) {
        Slice<Labor> laborSlice;

        if (keyword == null || keyword.isBlank()) {
            laborSlice = laborRepository.findAllByType(type, pageable);
        } else {
            laborSlice = laborRepository.findByNameContainingIgnoreCaseAndType(keyword, type, pageable);
        }

        return laborSlice.map(labor -> new LaborNameResponse(
                labor.getId(),
                labor.getName(),
                labor.getDailyWage(),
                labor.getPreviousDailyWage(),
                labor.getType().getLabel(),
                labor.getType()));
    }

    /**
     * 여러 인력정보 삭제
     */
    public void deleteLaborsByIds(DeleteLaborsRequest request) {
        List<Labor> labors = laborRepository.findAllById(request.laborIds());
        if (request.laborIds().size() != labors.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.LABOR_NOT_FOUND);
        }

        for (Labor labor : labors) {
            labor.markAsDeleted();
        }
        laborRepository.saveAll(labors);
    }

    /**
     * 인력정보 엑셀 다운로드
     */
    @Transactional(readOnly = true)
    public Workbook downloadExcel(LaborListRequest request, Sort sort, List<String> fields) {
        List<LaborListResponse> laborResponses = laborRepository.findAllWithoutPaging(request, sort);

        return ExcelExportUtils.generateWorkbook(
                laborResponses,
                fields,
                this::getExcelHeaderName,
                this::getExcelCellValue);
    }

    /**
     * 특정 인력정보 조회
     */
    @Transactional(readOnly = true)
    public LaborDetailResponse getLaborById(Long id) {
        Labor labor = laborRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.LABOR_NOT_FOUND));
        return LaborDetailResponse.from(labor);
    }

    /**
     * 인력정보 수정
     */
    @Transactional
    public void updateLabor(Long id, LaborUpdateRequest request) {
        Labor labor = getLaborByIdOrThrow(id);

        // 주민등록번호 중복 체크 (현재 인력 제외)
        if (laborRepository.existsByResidentNumberExcludingId(request.residentNumber(), id)) {
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

        // 변경 전 상태 저장 (Javers 스냅샷)
        labor.syncTransientFields();
        Labor oldSnapshot = JaversUtils.createSnapshot(javers, labor, Labor.class);

        // 기본 정보 업데이트
        labor.updateFrom(request, outsourcingCompany, isHeadOffice);

        labor.syncTransientFields();
        laborRepository.save(labor);

        // Javers를 사용하여 변경사항 추적
        Diff diff = javers.compare(oldSnapshot, labor);
        List<Map<String, String>> simpleChanges = JaversUtils.extractModifiedChanges(javers, diff);
        String changesJson = javers.getJsonConverter().toJson(simpleChanges);

        // 변경사항이 있을 때만 수정이력 생성
        if (!simpleChanges.isEmpty()) {
            LaborChangeHistory changeHistory = LaborChangeHistory.builder()
                    .labor(labor)
                    .type(LaborChangeType.BASIC)
                    .changes(changesJson)
                    .build();
            laborChangeHistoryRepository.save(changeHistory);
        }

        // 첨부파일 처리
        laborFileService.updateLaborFiles(labor, request.files());

        // 변경이력 메모 수정 처리
        if (request.changeHistories() != null && !request.changeHistories().isEmpty()) {
            for (LaborUpdateRequest.ChangeHistoryRequest historyRequest : request.changeHistories()) {
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
    private String getExcelHeaderName(String field) {
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
            case "hasBankbook" -> "통장사본";
            case "hasIdCard" -> "신분증사본";
            case "hasSignatureImage" -> "서명이미지";
            case "hasFile" -> "기타첨부";
            default -> null;
        };
    }

    /**
     * 엑셀 셀 값 반환
     */
    private String getExcelCellValue(LaborListResponse labor, String field) {
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
            case "dailyWage" -> labor.dailyWage() != null ? labor.dailyWage().toString() : "";
            case "accountNumber" -> {
                String bankName = labor.bankName() != null ? labor.bankName() : "";
                String accountNumber = labor.accountNumber() != null ? labor.accountNumber() : "";
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
    public Labor getLaborByIdOrThrow(Long id) {
        // -1인 경우 null 반환 (연관관계 끊기)
        if (id != null && id == -1L) {
            return null;
        }
        return laborRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(ValidationMessages.LABOR_NOT_FOUND));
    }

    /**
     * 인력정보 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<LaborListResponse> getLaborList(LaborListRequest request, Pageable pageable) {
        return laborRepository.findAll(request, pageable);
    }

    /**
     * 인력정보 변경 이력 조회
     */
    @Transactional(readOnly = true)
    public Slice<LaborChangeHistoryResponse> getLaborChangeHistories(Long id, Pageable pageable) {
        // 인력정보 존재 확인
        Labor labor = getLaborByIdOrThrow(id);

        // LaborChangeHistory를 labor 기준으로 슬라이스 조회
        Slice<LaborChangeHistory> historySlice = laborChangeHistoryRepository.findByLabor(labor, pageable);

        // 엔티티를 DTO로 변환하여 슬라이스 반환
        return historySlice.map(LaborChangeHistoryResponse::from);
    }
}
