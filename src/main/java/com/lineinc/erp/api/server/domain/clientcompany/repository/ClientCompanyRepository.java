package com.lineinc.erp.api.server.domain.clientcompany.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.clientcompany.entity.ClientCompany;

@Repository
public interface ClientCompanyRepository extends JpaRepository<ClientCompany, Long>, ClientCompanyRepositoryCustom {

    /**
     * 발주처명으로 검색하여 슬라이스 조회
     * keyword가 null이면 전체 조회
     */
    @Query("""
            SELECT cc FROM ClientCompany cc
            WHERE cc.deleted = false
              AND (:keyword IS NULL OR cc.name LIKE %:keyword%)
            """)
    Slice<ClientCompany> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 사업자등록번호 중복 확인
     * 
     * @param businessNumber 확인할 사업자등록번호
     * @return 중복 여부
     */
    @Query("""
            SELECT CASE WHEN COUNT(cc) > 0 THEN true ELSE false END
            FROM   ClientCompany cc
            WHERE  cc.businessNumber = :businessNumber
               AND cc.deleted = false
            """)
    boolean existsByBusinessNumber(@Param("businessNumber") String businessNumber);

}
