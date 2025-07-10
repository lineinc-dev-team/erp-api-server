package com.lineinc.erp.api.server.domain.menu.repository;

import com.lineinc.erp.api.server.domain.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    Optional<Menu> findByName(String name);

    @Query("SELECT m FROM Menu m LEFT JOIN FETCH m.permissions")
    List<Menu> findAllWithPermissions();
}


