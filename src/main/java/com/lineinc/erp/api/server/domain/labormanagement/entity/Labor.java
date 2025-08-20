package com.lineinc.erp.api.server.domain.labormanagement.entity;

import java.time.OffsetDateTime;
import java.util.List;

import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.labormanagement.enums.LaborType;
import com.lineinc.erp.api.server.domain.labormanagement.enums.WorkType;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;

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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
public class Labor extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "labor_seq")
    @SequenceGenerator(name = "labor_seq", sequenceName = "labor_seq", allocationSize = 1)
    private Long id;

    /**
     * 노무 구분
     */
    @DiffInclude
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
    @DiffInclude
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkType workType;

    /**
     * 공종 설명
     */
    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String workTypeDescription;

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
    @DiffInclude
    @Column
    private OffsetDateTime hireDate;

    /**
     * 퇴사일
     */
    @DiffInclude
    @Column
    private OffsetDateTime resignationDate;

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
    @DiffInclude
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
     * 파일 목록을 설정합니다.
     */
    public void setFiles(List<LaborFile> files) {
        this.files = files;
        if (files != null) {
            files.forEach(file -> file.setLabor(this));
        }
    }
}
