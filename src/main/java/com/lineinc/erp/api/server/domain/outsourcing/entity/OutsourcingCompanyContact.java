package com.lineinc.erp.api.server.domain.outsourcing.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.common.entity.interfaces.UpdatableFrom;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyContactUpdateRequest;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
@Table(indexes = {
        @Index(columnList = "name"),
        @Index(columnList = "email"),
        @Index(columnList = "phone_number"),
        @Index(columnList = "created_at"),
})
public class OutsourcingCompanyContact extends BaseEntity
        implements UpdatableFrom<OutsourcingCompanyContactUpdateRequest> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "outsourcing_company_contact_seq")
    @SequenceGenerator(name = "outsourcing_company_contact_seq", sequenceName = "outsourcing_company_contact_seq", allocationSize = 1)
    private Long id;

    /**
     * 이 담당자가 속한 발주처 엔티티
     */
    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outsourcing_company_id", nullable = false)
    private OutsourcingCompany outsourcingCompany;

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
    @DiffInclude
    @Column
    @Builder.Default
    private Boolean isMain = false;

    @Override
    public void updateFrom(OutsourcingCompanyContactUpdateRequest request) {
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