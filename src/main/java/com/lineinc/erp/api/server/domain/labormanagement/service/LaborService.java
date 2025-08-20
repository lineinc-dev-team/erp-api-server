package com.lineinc.erp.api.server.domain.labormanagement.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.labormanagement.entity.Labor;
import com.lineinc.erp.api.server.domain.labormanagement.entity.LaborFile;
import com.lineinc.erp.api.server.domain.labormanagement.repository.LaborRepository;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcing.service.OutsourcingCompanyService;
import com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.request.LaborCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.request.LaborFileRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.request.LaborListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.response.LaborListResponse;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Transactional
public class LaborService {

    private final LaborRepository laborRepository;
    private final OutsourcingCompanyService outsourcingCompanyService;

    /**
     * 노무 등록
     */
    public void createLabor(LaborCreateRequest request) {
        // 이름과 주민등록번호 중복 체크
        if (laborRepository.existsByNameAndResidentNumber(request.name(), request.residentNumber())) {
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

        laborRepository.save(labor);

        // 첨부파일 처리
        if (request.files() != null && !request.files().isEmpty()) {
            List<LaborFile> laborFiles = request.files().stream()
                    .map(fileRequest -> createLaborFile(labor, fileRequest))
                    .collect(Collectors.toList());

            // Labor 엔티티에 파일 목록 설정 (양방향 관계)
            labor.setFiles(laborFiles);
        }
    }

    /**
     * 노무 파일 생성
     */
    private LaborFile createLaborFile(Labor labor, LaborFileRequest fileRequest) {
        return LaborFile.builder()
                .labor(labor)
                .name(fileRequest.name())
                .fileUrl(fileRequest.fileUrl())
                .originalFileName(fileRequest.originalFileName())
                .memo(fileRequest.memo())
                .build();
    }

    /**
     * ID로 노무 조회 (삭제되지 않은 것만)
     */
    @Transactional(readOnly = true)
    public Labor getLaborByIdOrThrow(Long id) {
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
}
