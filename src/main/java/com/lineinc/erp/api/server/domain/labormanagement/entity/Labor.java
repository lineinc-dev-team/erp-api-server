package com.lineinc.erp.api.server.domain.labormanagement.entity;

import java.time.OffsetDateTime;
import java.util.List;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.labormanagement.enums.FileType;
import com.lineinc.erp.api.server.domain.labormanagement.enums.LaborType;
import com.lineinc.erp.api.server.domain.labormanagement.enums.WorkType;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.request.LaborUpdateRequest;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
import java.util.Optional;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class Labor extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "labor_seq")
    @SequenceGenerator(name = "labor_seq", sequenceName = "labor_seq", allocationSize = 1)
    private Long id;

    /**
     * 노무 구분
     */
    @DiffIgnore
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LaborType type;

    /**
     * 구분 설명
     */
    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String typeDescription;

    /**
     * 이름
     */
    @DiffInclude
    @Column(nullable = false)
    private String name;

    /**
     * 공종
     */
    @DiffIgnore
    @Enumerated(EnumType.STRING)
    @Column
    private WorkType workType;

    /**
     * 공종 설명
     */
    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String workTypeDescription;

    /**
     * 본사 인력 여부
     */
    @DiffIgnore
    @Column(nullable = false)
    @Builder.Default
    private Boolean isHeadOffice = false;

    /**
     * 임시 인력 여부
     */
    @DiffIgnore
    @Column
    @Builder.Default
    private Boolean isTemporary = false;

    /**
     * 주작업
     */
    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String mainWork;

    /**
     * 기준일당
     */
    @DiffInclude
    @Column
    private Long dailyWage;

    /**
     * 이전단가
     */
    @DiffIgnore
    @Column
    private Long previousDailyWage;

    /**
     * 근속일수
     */
    @DiffIgnore
    @Column
    private Long tenureDays;

    /**
     * 은행명
     */
    @DiffInclude
    @Column
    private String bankName;

    /**
     * 계좌번호
     */
    @DiffInclude
    @Column
    private String accountNumber;

    /**
     * 예금주
     */
    @DiffInclude
    @Column
    private String accountHolder;

    /**
     * 입사일
     */
    @DiffIgnore
    @Column
    private OffsetDateTime hireDate;

    /**
     * 퇴사일
     */
    @DiffIgnore
    @Column
    private OffsetDateTime resignationDate;

    /**
     * 첫 근무 시작일
     */
    @DiffInclude
    @Column
    private OffsetDateTime firstWorkDate;

    /**
     * 퇴직금 발생 요건 기준일
     */
    @DiffInclude
    @Column
    private OffsetDateTime severancePayEligibilityDate;

    /**
     * 외주업체 연결 (용역, 현장계약직인 경우)
     */
    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outsourcing_company_id")
    private OutsourcingCompany outsourcingCompany;

    /**
     * 주민등록번호
     */
    @DiffIgnore
    @Column
    private String residentNumber;

    /**
     * 주소
     */
    @DiffInclude
    @Column
    private String address;

    /**
     * 상세주소
     */
    @DiffInclude
    @Column
    private String detailAddress;

    /**
     * 휴대폰
     */
    @DiffInclude
    @Column
    private String phoneNumber;

    /**
     * 비고
     */
    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String memo;

    /**
     * 첨부파일 목록
     */
    @DiffIgnore
    @OneToMany(mappedBy = "labor", fetch = FetchType.LAZY, cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private List<LaborFile> files;

    /**
     * 변경 이력 목록
     */
    @DiffIgnore
    @OneToMany(mappedBy = "labor", fetch = FetchType.LAZY, cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private List<LaborChangeHistory> changeHistories;

    /**
     * 파일 목록을 설정합니다.
     */
    public void setFiles(List<LaborFile> files) {
        this.files = files;
        if (files != null) {
            files.forEach(file -> file.setLabor(this));
        }
    }

    /**
     * LaborUpdateRequest DTO로부터 인력정보를 업데이트합니다.
     */
    public void updateFrom(LaborUpdateRequest request, OutsourcingCompany outsourcingCompany, Boolean isHeadOffice) {
        Optional.ofNullable(request.type()).ifPresent(val -> this.type = val);
        Optional.ofNullable(request.typeDescription()).ifPresent(val -> this.typeDescription = val);
        Optional.ofNullable(request.name()).ifPresent(val -> this.name = val);
        Optional.ofNullable(request.residentNumber()).ifPresent(val -> this.residentNumber = val);
        Optional.ofNullable(request.workType()).ifPresent(val -> this.workType = val);
        Optional.ofNullable(request.workTypeDescription()).ifPresent(val -> this.workTypeDescription = val);
        Optional.ofNullable(request.mainWork()).ifPresent(val -> this.mainWork = val);
        Optional.ofNullable(request.dailyWage()).ifPresent(val -> this.dailyWage = val);
        Optional.ofNullable(request.bankName()).ifPresent(val -> this.bankName = val);
        Optional.ofNullable(request.accountNumber()).ifPresent(val -> this.accountNumber = val);
        Optional.ofNullable(request.accountHolder()).ifPresent(val -> this.accountHolder = val);
        Optional.ofNullable(request.hireDate())
                .ifPresent(val -> this.hireDate = DateTimeFormatUtils.toOffsetDateTime(val));
        Optional.ofNullable(request.resignationDate())
                .ifPresent(val -> this.resignationDate = DateTimeFormatUtils.toOffsetDateTime(val));
        Optional.ofNullable(request.address()).ifPresent(val -> this.address = val);
        Optional.ofNullable(request.detailAddress()).ifPresent(val -> this.detailAddress = val);
        Optional.ofNullable(request.phoneNumber()).ifPresent(val -> this.phoneNumber = val);
        Optional.ofNullable(request.memo()).ifPresent(val -> this.memo = val);

        // 외주업체 정보와 본사 인력 여부 업데이트
        Optional.ofNullable(outsourcingCompany).ifPresent(val -> this.outsourcingCompany = val);
        Optional.ofNullable(isHeadOffice).ifPresent(val -> this.isHeadOffice = val);
    }

    /**
     * 이전단가를 업데이트합니다.
     */
    public void updatePreviousDailyWage(Long previousDailyWage) {
        this.previousDailyWage = previousDailyWage;
    }

    /**
     * 첫 근무 시작일을 설정합니다.
     */
    public void setFirstWorkDate(OffsetDateTime firstWorkDate) {
        this.firstWorkDate = firstWorkDate;
    }

    /**
     * 퇴직금 발생 요건 기준일을 설정합니다.
     */
    public void setSeverancePayEligibilityDate(OffsetDateTime severancePayEligibilityDate) {
        this.severancePayEligibilityDate = severancePayEligibilityDate;
    }

    /**
     * 첨부파일 존재 여부를 반환합니다.
     * 타입이 기본인 파일 중에 fileUrl이 하나라도 있으면 true를 반환합니다.
     */
    public Boolean getHasFile() {
        return files != null && files.stream()
                .anyMatch(file -> FileType.BASIC.equals(file.getType()) &&
                        file.getFileUrl() != null && !file.getFileUrl().trim().isEmpty());
    }

    /**
     * 통장사본 첨부파일 존재 여부를 반환합니다.
     */
    public Boolean getHasBankbook() {
        return files != null && files.stream()
                .anyMatch(file -> FileType.BANKBOOK.equals(file.getType()) &&
                        file.getFileUrl() != null && !file.getFileUrl().trim().isEmpty());
    }

    /**
     * 신분증 사본 첨부파일 존재 여부를 반환합니다.
     */
    public Boolean getHasIdCard() {
        return files != null && files.stream()
                .anyMatch(file -> FileType.ID_CARD.equals(file.getType()) &&
                        file.getFileUrl() != null && !file.getFileUrl().trim().isEmpty());
    }

    /**
     * 서명이미지 첨부파일 존재 여부를 반환합니다.
     */
    public Boolean getHasSignatureImage() {
        return files != null && files.stream()
                .anyMatch(file -> FileType.SIGNATURE_IMAGE.equals(file.getType()) &&
                        file.getFileUrl() != null && !file.getFileUrl().trim().isEmpty());
    }

    @Transient
    @DiffInclude
    private String outsourcingCompanyName;

    @Transient
    @DiffInclude
    private String typeName;

    @Transient
    @DiffInclude
    private String workTypeName;

    @Transient
    @DiffInclude
    private String hireDateFormat;

    @Transient
    @DiffInclude
    private String resignationDateFormat;

    @Transient
    @DiffInclude
    private String severancePayEligibilityDateFormat;

    /**
     * 연관 엔티티에서 이름 값을 복사해 transient 필드에 세팅
     */
    public void syncTransientFields() {
        if (this.isHeadOffice != null && this.isHeadOffice) {
            this.outsourcingCompanyName = AppConstants.LINE_INC_NAME;
        } else {
            this.outsourcingCompanyName = this.outsourcingCompany != null ? this.outsourcingCompany.getName() : null;
        }
        this.typeName = this.type != null ? this.type.getLabel() : null;
        this.workTypeName = this.workType != null ? this.workType.getLabel() : null;
        this.hireDateFormat = this.hireDate != null
                ? DateTimeFormatUtils.formatKoreaLocalDate(this.hireDate)
                : null;
        this.resignationDateFormat = this.resignationDate != null
                ? DateTimeFormatUtils.formatKoreaLocalDate(this.resignationDate)
                : null;
    }
}
