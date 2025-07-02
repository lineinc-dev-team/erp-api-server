package com.lineinc.erp.api.server.domain.client;

import com.lineinc.erp.api.server.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ClientCompanyFile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_company_file_seq")
    @SequenceGenerator(name = "client_company_file_seq", sequenceName = "client_company_file_seq", allocationSize = 1)
    private Long id;

    /**
     * 이 파일이 연결된 발주처 엔티티
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_company_id", nullable = false)
    private ClientCompany clientCompany;

    /**
     * 문서명 (사용자가 지정하는 파일 이름)
     */
    @Column(nullable = false)
    private String documentName;

    /**
     * S3 또는 외부 스토리지에 저장된 파일의 URL
     */
    @Column(nullable = false)
    private String fileUrl; // S3 경로

    /**
     * 업로드된 파일의 원본 파일명
     */
    @Column
    private String originalFileName;

    /**
     * 파일에 대한 비고 또는 설명
     */
    @Column(columnDefinition = "TEXT")
    private String memo; // 비고 / 메모
}
