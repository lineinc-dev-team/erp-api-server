//package com.lineinc.erp.api.server.domain.outsourcing.entity;
//
//import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
//import jakarta.persistence.*;
//import lombok.*;
//import lombok.experimental.SuperBuilder;
//
//@Entity
//@Table(indexes = {
//        @Index(columnList = "name")
//})
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@AllArgsConstructor
//@SuperBuilder
//public class OutsourcingCompany extends BaseEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "outsourcing_company_seq")
//    @SequenceGenerator(name = "outsourcing_company_seq", sequenceName = "outsourcing_company_seq", allocationSize = 1)
//    private Long id;
//
//    @Column(nullable = false)
//    private String name;
//
//    @Column
//    private String businessNumber;
//
//    @Column
//    private String ceoName;
//
//    @Column
//    private String address;
//
//    @Column
//    private String landlineNumber;
//
//    @Column
//    private String phoneNumber;
//
//    @Column
//    private String email;
//
//    /**
//     * 보증서 제출 여부
//     */
//    @Column(nullable = false)
//    @Builder.Default
//    private boolean guaranteeSubmitted = false;
//
//    @Builder.Default
//    @Column(nullable = false)
//    private boolean isActive = true;
//
//    @Column(columnDefinition = "TEXT")
//    private String memo;
//
/// /    // 담당자 및 파일 같은 연관 관계는 이후 필요 시 추가 가능
/// /    // 예: List<OutsourcingCompanyContact>, List<OutsourcingCompanyFile> 등
/// /
/// /    public void updateFrom(OutsourcingCompanyUpdateRequest request) {
/// /        Optional.ofNullable(request.name()).ifPresent(val -> this.name = val);
/// /        Optional.ofNullable(request.businessNumber()).ifPresent(val -> this.businessNumber = val);
/// /        Optional.ofNullable(request.ceoName()).ifPresent(val -> this.ceoName = val);
/// /        Optional.ofNullable(request.address()).ifPresent(val -> this.address = val);
/// /        Optional.ofNullable(request.landlineNumber()).ifPresent(val -> this.landlineNumber = val);
/// /        Optional.ofNullable(request.phoneNumber()).ifPresent(val -> this.phoneNumber = val);
/// /        Optional.ofNullable(request.email()).ifPresent(val -> this.email = val);
/// /        Optional.ofNullable(request.paymentMethod()).ifPresent(val -> this.paymentMethod = val);
/// /        Optional.ofNullable(request.paymentPeriod()).ifPresent(val -> this.paymentPeriod = val);
/// /        Optional.ofNullable(request.memo()).ifPresent(val -> this.memo = val);
/// /        Optional.ofNullable(request.isActive()).ifPresent(val -> this.isActive = val);
/// /    }
//}