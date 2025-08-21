package com.lineinc.erp.api.server.domain.labormanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import com.lineinc.erp.api.server.domain.labormanagement.entity.Labor;
import com.lineinc.erp.api.server.domain.labormanagement.enums.LaborType;

import java.util.List;

@Repository
public interface LaborRepository extends JpaRepository<Labor, Long>, LaborRepositoryCustom {

    /**
     * 이름과 주민등록번호로 중복 체크
     */
    boolean existsByNameAndResidentNumber(String name, String residentNumber);

    /**
     * 특정 ID를 제외하고 이름과 주민등록번호로 중복 체크
     */
    @Query("SELECT COUNT(l) > 0 FROM Labor l WHERE l.name = :name AND l.residentNumber = :residentNumber AND l.id != :excludeId")
    boolean existsByNameAndResidentNumberExcludingId(@Param("name") String name,
            @Param("residentNumber") String residentNumber, @Param("excludeId") Long excludeId);

    /**
     * 특정 type의 typeDescription 값들을 조회
     */
    @Query("SELECT DISTINCT l.typeDescription, l.id FROM Labor l WHERE l.type = :type AND l.typeDescription IS NOT NULL")
    Slice<Object[]> findAllDistinctTypeDescriptions(@Param("type") LaborType type, Pageable pageable);

    /**
     * 특정 type의 typeDescription 값들을 키워드로 검색
     */
    @Query("SELECT DISTINCT l.typeDescription, l.id FROM Labor l WHERE l.type = :type AND l.typeDescription IS NOT NULL AND l.typeDescription LIKE %:keyword%")
    Slice<Object[]> findDistinctTypeDescriptionsByKeyword(@Param("type") LaborType type,
            @Param("keyword") String keyword, Pageable pageable);
}
