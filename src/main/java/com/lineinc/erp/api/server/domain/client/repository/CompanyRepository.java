package com.lineinc.erp.api.server.domain.client.repository;

import com.lineinc.erp.api.server.domain.client.entity.ClientCompany;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<ClientCompany, Long>, CompanyRepositoryCustom {
    @Query("SELECT cc FROM ClientCompany cc WHERE LOWER(cc.name) LIKE LOWER(CONCAT('%', :name, '%')) AND cc.deleted = false")
    Slice<ClientCompany> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    @Query("SELECT cc FROM ClientCompany cc WHERE cc.deleted = false")
    Slice<ClientCompany> findAllBy(Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(cc) > 0 THEN true ELSE false END FROM ClientCompany cc WHERE cc.businessNumber = :businessNumber AND cc.deleted = false")
    boolean existsByBusinessNumber(@Param("businessNumber") String businessNumber);
}
