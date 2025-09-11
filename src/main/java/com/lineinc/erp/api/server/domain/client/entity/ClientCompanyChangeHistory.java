package com.lineinc.erp.api.server.domain.client.entity;

import com.lineinc.erp.api.server.domain.client.enums.ClientCompanyChangeHistoryChangeType;
import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(indexes = {
        @Index(columnList = "createdAt"),
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class ClientCompanyChangeHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_company_change_history_seq")
    @SequenceGenerator(name = "client_company_change_history_seq", sequenceName = "client_company_change_history_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_company_id", nullable = false)
    private ClientCompany clientCompany;

    @Enumerated(EnumType.STRING)
    @Column
    private ClientCompanyChangeHistoryChangeType type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private String changes;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String memo; // 선택적 변경 사유, 비고 등
}
