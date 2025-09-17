package com.lineinc.erp.api.server.domain.client.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.client.entity.ClientCompany;

@Repository
public interface CompanyRepository extends JpaRepository<ClientCompany, Long>, CompanyRepositoryCustom {

    /**
     * 발주처명으로 검색 (대소문자 무시)
     * 
     * @param name     검색할 발주처명
     * @param pageable 페이징 정보
     * @return 검색된 발주처 슬라이스
     */
    @Query("SELECT cc FROM ClientCompany cc WHERE LOWER(cc.name) LIKE LOWER(CONCAT('%', :name, '%')) AND cc.deleted = false")
    Slice<ClientCompany> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    /**
     * 삭제되지 않은 모든 발주처 조회
     * 
     * @param pageable 페이징 정보
     * @return 발주처 슬라이스
     */
    @Query("SELECT cc FROM ClientCompany cc WHERE cc.deleted = false")
    Slice<ClientCompany> findAllBy(Pageable pageable);

    /**
     * 사업자등록번호 중복 확인
     * 
     * @param businessNumber 확인할 사업자등록번호
     * @return 중복 여부
     */
    @Query("SELECT CASE WHEN COUNT(cc) > 0 THEN true ELSE false END FROM ClientCompany cc WHERE cc.businessNumber = :businessNumber AND cc.deleted = false")
    boolean existsByBusinessNumber(@Param("businessNumber") String businessNumber);

}
