package com.lineinc.erp.api.server.domain.menu.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.menu.entity.Menu;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    Optional<Menu> findByName(String name);

    @Query("SELECT m FROM Menu m LEFT JOIN FETCH m.permissions p WHERE m.deleted = false ORDER BY m.order, p.order")
    List<Menu> findAllWithPermissions();
}
