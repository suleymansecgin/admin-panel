package com.suleymansecgin.admin_panel.service;

import com.suleymansecgin.admin_panel.dto.*;
import com.suleymansecgin.admin_panel.exception.BaseException;
import com.suleymansecgin.admin_panel.exception.ErrorMessage;
import com.suleymansecgin.admin_panel.exception.MessageType;
import com.suleymansecgin.admin_panel.model.Permission;
import com.suleymansecgin.admin_panel.model.Role;
import com.suleymansecgin.admin_panel.repository.PermissionRepository;
import com.suleymansecgin.admin_panel.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleService {
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private PermissionRepository permissionRepository;
	
	@Transactional(readOnly = true)
	public List<RoleResponse> getAllRoles() {
		return roleRepository.findAll().stream()
				.map(this::convertToResponse)
				.collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public RoleResponse getRoleById(Long id) {
		Role role = roleRepository.findById(id)
				.orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.ROLE_NOT_FOUND, null)));
		return convertToResponse(role);
	}
	
	@Transactional(readOnly = true)
	public RoleResponse getRoleByCode(String code) {
		Role role = roleRepository.findByCodeWithPermissions(code)
				.orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.ROLE_NOT_FOUND, null)));
		return convertToResponse(role);
	}
	
	@Transactional
	public RoleResponse createRole(RoleRequest request) {
		if (roleRepository.existsByCode(request.getCode())) {
			throw new BaseException(new ErrorMessage(MessageType.VALIDATION_ERROR, "Rol kodu zaten kullanılıyor"));
		}
		
		Role role = new Role();
		role.setCode(request.getCode());
		role.setName(request.getName());
		role.setDescription(request.getDescription());
		
		// Permission'ları ata
		if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
			Set<Permission> permissions = request.getPermissionIds().stream()
					.map(permissionId -> permissionRepository.findById(permissionId)
							.orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.PERMISSION_NOT_FOUND, null))))
					.collect(Collectors.toSet());
			role.setPermissions(permissions);
		}
		
		role = roleRepository.save(role);
		return convertToResponse(role);
	}
	
	@Transactional
	public RoleResponse updateRole(Long id, RoleRequest request) {
		Role role = roleRepository.findById(id)
				.orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.ROLE_NOT_FOUND, null)));
		
		// Kod değişikliği kontrolü
		if (!role.getCode().equals(request.getCode()) && roleRepository.existsByCode(request.getCode())) {
			throw new BaseException(new ErrorMessage(MessageType.VALIDATION_ERROR, "Rol kodu zaten kullanılıyor"));
		}
		
		role.setCode(request.getCode());
		role.setName(request.getName());
		role.setDescription(request.getDescription());
		
		// Permission'ları güncelle
		if (request.getPermissionIds() != null) {
			Set<Permission> permissions = request.getPermissionIds().stream()
					.map(permissionId -> permissionRepository.findById(permissionId)
							.orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.PERMISSION_NOT_FOUND, null))))
					.collect(Collectors.toSet());
			role.setPermissions(permissions);
		}
		
		role = roleRepository.save(role);
		return convertToResponse(role);
	}
	
	@Transactional
	public void deleteRole(Long id) {
		Role role = roleRepository.findById(id)
				.orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.ROLE_NOT_FOUND, null)));
		
		// Kullanıcılara atanmış rol kontrolü
		if (!role.getUsers().isEmpty()) {
			throw new BaseException(new ErrorMessage(MessageType.VALIDATION_ERROR, "Bu rol kullanıcılara atanmış, silinemez"));
		}
		
		roleRepository.delete(role);
	}
	
	@Transactional(readOnly = true)
	public List<PermissionResponse> getAllPermissions() {
		return permissionRepository.findAll().stream()
				.map(permission -> {
					PermissionResponse response = new PermissionResponse();
					response.setId(permission.getId());
					response.setCode(permission.getCode());
					response.setName(permission.getName());
					response.setDescription(permission.getDescription());
					return response;
				})
				.collect(Collectors.toList());
	}
	
	private RoleResponse convertToResponse(Role role) {
		RoleResponse response = new RoleResponse();
		response.setId(role.getId());
		response.setCode(role.getCode());
		response.setName(role.getName());
		response.setDescription(role.getDescription());
		
		Set<RoleResponse.PermissionResponse> permissions = role.getPermissions().stream()
				.map(permission -> {
					RoleResponse.PermissionResponse permResponse = new RoleResponse.PermissionResponse();
					permResponse.setId(permission.getId());
					permResponse.setCode(permission.getCode());
					permResponse.setName(permission.getName());
					permResponse.setDescription(permission.getDescription());
					return permResponse;
				})
				.collect(Collectors.toSet());
		
		response.setPermissions(permissions);
		return response;
	}
}

