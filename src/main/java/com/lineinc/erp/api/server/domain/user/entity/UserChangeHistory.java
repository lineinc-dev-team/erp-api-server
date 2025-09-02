package com.lineinc.erp.api.server.domain.user.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.user.enums.UserChangeType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(indexes = {
        @Index(columnList = "type"),
        @Index(columnList = "created_at"),
        @Index(columnList = "user_id,created_at")
})
@Getter
@NoArgsConstructor
@SuperBuilder
public class UserChangeHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_change_history_seq")
    @SequenceGenerator(name = "user_change_history_seq", sequenceName = "user_change_history_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserChangeType type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private String changes;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String memo; // 선택적 변경 사유, 비고 등
}
