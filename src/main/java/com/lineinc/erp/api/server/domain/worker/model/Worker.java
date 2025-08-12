//package com.lineinc.erp.api.server.domain.worker.entity;
//
//import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
//import com.lineinc.erp.api.server.domain.worker.enums.WorkerType;
//import jakarta.persistence.*;
//import lombok.*;
//
//@Entity
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@AllArgsConstructor
//@Builder
//public class Worker extends BaseEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "worker_seq")
//    @SequenceGenerator(name = "worker_seq", sequenceName = "worker_seq", allocationSize = 1)
//    private Long id;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private WorkerType type; // 상용직, 일용직 등
//
//    @Column(nullable = false)
//    private String name;
//
//    @Column(nullable = false)
//    private String residentNumber; // 주민등록번호, 보안 고려 필요
//
//    @Column
//    private String phoneNumber;
//
//    @Column(columnDefinition = "TEXT")
//    private String memo;
//
//    @Enumerated(EnumType.STRING)
//    private ConstructionType constructionType;
//
//    @Column
//    private String constructionDescription;
//
//    @Column(columnDefinition = "TEXT")
//    private String mainTask;
//
//    @Column(precision = 10, scale = 2)
//    private BigDecimal dailyWage;
//
//    @Column
//    private LocalDate joinDate;
//
//    @Column
//    private LocalDate retireDate;
//}