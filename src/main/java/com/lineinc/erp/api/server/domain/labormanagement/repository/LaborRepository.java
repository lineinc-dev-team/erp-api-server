package com.lineinc.erp.api.server.domain.labormanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.lineinc.erp.api.server.domain.labormanagement.entity.Labor;
import com.lineinc.erp.api.server.domain.labormanagement.enums.LaborType;

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
    @Query("SELECT DISTINCT l.typeDescription, l.id FROM Labor l WHERE l.type = :type AND l.typeDescription IS NOT NULL AND l.deleted = false")
    Slice<Object[]> findAllDistinctTypeDescriptions(@Param("type") LaborType type, Pageable pageable);

    /**
     * 특정 type의 typeDescription 값들을 키워드로 검색
     */
    @Query("SELECT DISTINCT l.typeDescription, l.id FROM Labor l WHERE l.type = :type AND l.typeDescription IS NOT NULL AND l.deleted = false AND l.typeDescription LIKE %:keyword%")
    Slice<Object[]> findDistinctTypeDescriptionsByKeyword(@Param("type") LaborType type,
            @Param("keyword") String keyword, Pageable pageable);

    /**
     * 모든 인력 정보를 페이지네이션으로 조회
     */
    Slice<Labor> findAllBy(Pageable pageable);

    /**
     * 이름으로 키워드 검색하여 인력 정보를 페이지네이션으로 조회
     */
    Slice<Labor> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * 특정 노무인력 유형으로 인력 정보를 페이지네이션으로 조회
     */
    @Query("SELECT l FROM Labor l WHERE l.deleted = false AND (:type IS NULL OR l.type = :type)")
    Slice<Labor> findAllByType(@Param("type") LaborType type, Pageable pageable);

    /**
     * 이름으로 키워드 검색하고 특정 노무인력 유형으로 필터링하여 인력 정보를 페이지네이션으로 조회
     */
    @Query("SELECT l FROM Labor l WHERE l.deleted = false AND l.name LIKE %:name% AND (:type IS NULL OR l.type = :type)")
    Slice<Labor> findByNameContainingIgnoreCaseAndType(@Param("name") String name, @Param("type") LaborType type,
            Pageable pageable);
}
