package com.suleymansecgin.admin_panel.service;

import com.suleymansecgin.admin_panel.dto.*;
import com.suleymansecgin.admin_panel.exception.BaseException;
import com.suleymansecgin.admin_panel.exception.ErrorMessage;
import com.suleymansecgin.admin_panel.exception.MessageType;
import com.suleymansecgin.admin_panel.model.Role;
import com.suleymansecgin.admin_panel.model.User;
import com.suleymansecgin.admin_panel.repository.RoleRepository;
import com.suleymansecgin.admin_panel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Transactional(readOnly = true)
	public List<UserResponse> getAllUsers() {
		return userRepository.findAll().stream()
				.map(this::convertToResponse)
				.collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public UserResponse getUserById(Long id) {
		User user = userRepository.findByIdWithRoles(id)
				.orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, null)));
		return convertToResponse(user);
	}
	
	@Transactional
	public UserResponse assignRolesToUser(Long userId, UserRoleRequest request) {
		User user = userRepository.findByIdWithRoles(userId)
				.orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, null)));
		
		Set<Role> roles = request.getRoleIds().stream()
				.map(roleId -> roleRepository.findById(roleId)
						.orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.ROLE_NOT_FOUND, null))))
				.collect(Collectors.toSet());
		
		user.setRoles(roles);
		user = userRepository.save(user);
		
		return convertToResponse(user);
	}
	
	@Transactional
	public UserResponse updateUserStatus(Long userId, Boolean enabled) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, null)));
		
		user.setEnabled(enabled);
		user = userRepository.save(user);
		
		return convertToResponse(user);
	}
	
	private UserResponse convertToResponse(User user) {
		UserResponse response = new UserResponse();
		response.setId(user.getId());
		response.setUsername(user.getUsername());
		response.setEmail(user.getEmail());
		response.setFirstName(user.getFirstName());
		response.setLastName(user.getLastName());
		response.setEnabled(user.getEnabled());
		response.setMfaEnabled(user.getMfaEnabled());
		response.setCreatedAt(user.getCreatedAt());
		response.setLastLoginAt(user.getLastLoginAt());
		
		Set<UserResponse.RoleInfo> roles = user.getRoles().stream()
				.map(role -> {
					UserResponse.RoleInfo roleInfo = new UserResponse.RoleInfo();
					roleInfo.setId(role.getId());
					roleInfo.setCode(role.getCode());
					roleInfo.setName(role.getName());
					return roleInfo;
				})
				.collect(Collectors.toSet());
		
		response.setRoles(roles);
		return response;
	}
}

