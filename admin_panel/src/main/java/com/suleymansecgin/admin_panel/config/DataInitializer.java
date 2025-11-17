package com.suleymansecgin.admin_panel.config;

import com.suleymansecgin.admin_panel.enums.PermissionType;
import com.suleymansecgin.admin_panel.model.Permission;
import com.suleymansecgin.admin_panel.model.Role;
import com.suleymansecgin.admin_panel.repository.PermissionRepository;
import com.suleymansecgin.admin_panel.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
@Order(2)
public class DataInitializer implements CommandLineRunner {
	
	@Autowired
	private PermissionRepository permissionRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Override
	@Transactional
	public void run(String... args) throws Exception {
		initializePermissions();
		initializeRoles();
	}
	
	private void initializePermissions() {
		for (PermissionType permissionType : PermissionType.values()) {
			if (!permissionRepository.existsByCode(permissionType.getCode())) {
				Permission permission = new Permission();
				permission.setCode(permissionType.getCode());
				permission.setName(permissionType.getDescription());
				permission.setDescription(permissionType.getDescription());
				permissionRepository.save(permission);
				System.out.println("Permission oluşturuldu: " + permissionType.getCode());
			}
		}
	}
	
	private void initializeRoles() {
		// Yönetici rolü - Tüm yetkilere sahip
		if (!roleRepository.existsByCode("ADMIN")) {
			Role adminRole = new Role();
			adminRole.setCode("ADMIN");
			adminRole.setName("Yönetici");
			adminRole.setDescription("Tüm yetkilere sahip yönetici rolü");
			
			Set<Permission> allPermissions = new HashSet<>(permissionRepository.findAll());
			adminRole.setPermissions(allPermissions);
			
			roleRepository.save(adminRole);
			System.out.println("Yönetici rolü oluşturuldu");
		}
		
		// Çalışan rolü - Başlangıçta yetkisiz, yönetici tarafından yetkiler atanacak
		if (!roleRepository.existsByCode("EMPLOYEE")) {
			Role employeeRole = new Role();
			employeeRole.setCode("EMPLOYEE");
			employeeRole.setName("Çalışan");
			employeeRole.setDescription("Yönetici tarafından yetkiler atanacak çalışan rolü");
			
			// Başlangıçta yetki verilmez, yönetici tarafından atanacak
			employeeRole.setPermissions(new HashSet<>());
			roleRepository.save(employeeRole);
			System.out.println("Çalışan rolü oluşturuldu (yetkisiz)");
		}
	}
}

