package com.lineinc.erp.api.server.domain.labor.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.labor.entity.Labor;
import com.lineinc.erp.api.server.domain.labor.enums.LaborType;

@Repository
public interface LaborRepository extends JpaRepository<Labor, Long>, LaborRepositoryCustom {

    /**
     * ID로 노무 조회 (삭제되지 않은 것만)
     */
    @Query("SELECT l FROM Labor l WHERE l.id = :id AND l.deleted = false")
    Optional<Labor> findByIdAndNotDeleted(@Param("id") Long id);

    /**
     * 주민등록번호로 중복 체크 (삭제되지 않은 인력 중에서)
     */
    @Query("SELECT COUNT(l) > 0 FROM Labor l WHERE l.residentNumber = :residentNumber AND l.deleted = false")
    boolean existsByResidentNumber(@Param("residentNumber") String residentNumber);

    /**
     * 특정 ID를 제외하고 주민등록번호로 중복 체크 (삭제되지 않은 인력 중에서)
     */
    @Query("SELECT COUNT(l) > 0 FROM Labor l WHERE l.residentNumber = :residentNumber AND l.deleted = false AND l.id != :excludeId")
    boolean existsByResidentNumberExcludingId(@Param("residentNumber") String residentNumber,
            @Param("excludeId") Long excludeId);

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
     * 인력 정보를 페이지네이션으로 조회합니다.
     * 이름이 제공되면 해당 이름으로 검색하고, 노무인력 유형이 제공되면 해당 유형으로 필터링합니다.
     */
    @Query("""
            SELECT l FROM Labor l WHERE l.deleted = false \
            AND (:name IS NULL OR l.name LIKE %:name%) \
            AND (:types IS NULL OR l.type IN :types)""")
    Slice<Labor> findAllByNameAndType(@Param("name") String name,
            @Param("types") List<LaborType> types,
            Pageable pageable);

    /**
     * 근속일수를 업데이트합니다.
     */
    @Modifying
    @Query("UPDATE Labor l SET l.tenureDays = :tenureDays WHERE l.id = :laborId")
    void updateTenureDays(@Param("laborId") Long laborId, @Param("tenureDays") Long tenureDays);

    /**
     * 근속일수 산정을 위해 유효한 인력을 조회합니다.
     * 조건: 삭제되지 않음, 지정된 type, 첫 근무일 존재
     */
    @Query("""
            SELECT l FROM Labor l
            WHERE l.deleted = false
              AND (l.type = :type1 OR l.type = :type2)
              AND l.firstWorkDate IS NOT NULL
            """)
    List<Labor> findEligibleLaborsForTenureDaysCalculation(@Param("type1") LaborType type1,
            @Param("type2") LaborType type2);

}
