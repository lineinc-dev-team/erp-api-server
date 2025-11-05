package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity;

import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request.OutsourcingCompanyContractContactUpdateRequest;
import com.lineinc.erp.api.server.shared.constant.AppConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
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
@SQLRestriction("deleted = false")
public class OutsourcingCompanyContractContact extends BaseEntity {

    private static final String SEQUENCE_NAME = "outsourcing_company_contract_contact_seq";
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.OUTSOURCING_COMPANY_CONTRACT_ID, nullable = false)
    private OutsourcingCompanyContract outsourcingCompanyContract;

    /**
     * 담당자의 이름
     */
    @DiffInclude
    @Column(nullable = false)
    private String name;

    /**
     * 담당자의 부서
     */
    @DiffInclude
    @Column
    private String department;

    /**
     * 담당자의 직급
     */
    @DiffInclude
    @Column
    private String position;

    @DiffInclude
    @Column
    private String landlineNumber;

    @DiffInclude
    @Column
    private String phoneNumber;

    @DiffInclude
    @Column
    private String email;

    /**
     * 담당자에 대한 비고 또는 추가 메모
     */
    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String memo;

    /**
     * 대표 담당자인지 여부
     */
    @DiffIgnore
    @Column
    @Builder.Default
    private Boolean isMain = false;

    /**
     * DTO의 정보로 엔티티를 업데이트합니다.
     */
    public void updateFrom(final OutsourcingCompanyContractContactUpdateRequest request) {
        this.name = request.name();
        this.department = request.department();
        this.position = request.position();
        this.landlineNumber = request.landlineNumber();
        this.phoneNumber = request.phoneNumber();
        this.email = request.email();
        this.memo = request.memo();
        this.isMain = request.isMain();
    }

}