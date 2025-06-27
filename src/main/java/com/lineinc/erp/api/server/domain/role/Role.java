package com.lineinc.erp.api.server.domain.role;

import com.lineinc.erp.api.server.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Role extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String name;
}