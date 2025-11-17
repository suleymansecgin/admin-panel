package com.suleymansecgin.admin_panel.repository;

import com.suleymansecgin.admin_panel.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
	
	Optional<Permission> findByCode(String code);
	
	boolean existsByCode(String code);
}

