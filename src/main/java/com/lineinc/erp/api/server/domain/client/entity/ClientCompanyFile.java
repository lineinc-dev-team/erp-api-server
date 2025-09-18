package com.lineinc.erp.api.server.domain.client.entity;

import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.client.enums.ClientCompanyFileType;
import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request.ClientCompanyFileCreateRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request.ClientCompanyFileUpdateRequest;
import com.lineinc.erp.api.server.shared.constant.AppConstants;

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
import jakarta.persistence.SequenceGenerator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@SQLRestriction("deleted = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClientCompanyFile extends BaseEntity {
    private static final String SEQUENCE_NAME = "client_company_file_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.CLIENT_COMPANY_ID, nullable = false)
    private ClientCompany clientCompany;

    @Column
    @DiffInclude
    private String name;

    @Column
    @DiffIgnore
    private String fileUrl;

    @Column
    @DiffInclude
    private String originalFileName;

    @Column
    @Enumerated(EnumType.STRING)
    private ClientCompanyFileType type;

    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String memo;

    public void updateFrom(final ClientCompanyFileUpdateRequest request) {
        this.name = request.name();
        this.fileUrl = request.fileUrl();
        this.originalFileName = request.originalFileName();
        this.type = request.type();
        this.memo = request.memo();
    }

    public static ClientCompanyFile createFrom(final ClientCompanyFileCreateRequest request,
            final ClientCompany clientCompany) {
        return ClientCompanyFile.builder()
                .name(request.name())
                .fileUrl(request.fileUrl())
                .originalFileName(request.originalFileName())
                .type(request.type())
                .memo(request.memo())
                .clientCompany(clientCompany)
                .build();
    }

}
