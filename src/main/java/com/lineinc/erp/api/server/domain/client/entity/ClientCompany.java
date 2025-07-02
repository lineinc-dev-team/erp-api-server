package com.lineinc.erp.api.server.domain.client.entity;

import com.lineinc.erp.api.server.domain.client.enums.PaymentMethod;
import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ClientCompany extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_company_seq")
    @SequenceGenerator(name = "client_company_seq", sequenceName = "client_company_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String name;

    /**
     * 사업자등록번호
     */
    @Column
    private String businessNumber;

    @Column
    private String ceoName;

    /**
     * 본사 주소
     */
    @Column
    private String address;

    @Column
    private String landlineNumber;

    @Column
    private String email;

    /**
     * 결제 방식 (현금/어음)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    /**
     * 결제 유예 기간
     */
    @Column
    private String paymentPeriod;

    /**
     * 발주처 담당자 목록
     */
    @OneToMany(mappedBy = "clientCompany", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ClientCompanyContact> contacts = new ArrayList<>();

    /**
     * 발주처 관련 첨부파일 목록
     */
    @OneToMany(mappedBy = "clientCompany", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ClientCompanyFile> files = new ArrayList<>();

    /**
     * 사용 여부
     */
    @Builder.Default
    @Column(nullable = false)
    private boolean isActive = true;

    /**
     * 비고 / 메모
     */
    @Column(columnDefinition = "TEXT")
    private String memo;
}