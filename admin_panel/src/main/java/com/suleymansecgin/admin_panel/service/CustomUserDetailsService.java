package com.suleymansecgin.admin_panel.service;

import com.suleymansecgin.admin_panel.exception.BaseException;
import com.suleymansecgin.admin_panel.exception.ErrorMessage;
import com.suleymansecgin.admin_panel.exception.MessageType;
import com.suleymansecgin.admin_panel.model.User;
import com.suleymansecgin.admin_panel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsernameWithRolesAndPermissions(username)
				.orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, username)));
		
		if (!user.getEnabled()) {
			throw new BaseException(new ErrorMessage(MessageType.USER_DISABLED, null));
		}
		
		if (!user.getAccountNonLocked()) {
			throw new BaseException(new ErrorMessage(MessageType.USER_LOCKED, null));
		}
		
		if (!user.getAccountNonExpired()) {
			throw new BaseException(new ErrorMessage(MessageType.USER_EXPIRED, null));
		}
		
		if (!user.getCredentialsNonExpired()) {
			throw new BaseException(new ErrorMessage(MessageType.CREDENTIALS_EXPIRED, null));
		}
		
		return org.springframework.security.core.userdetails.User.builder()
				.username(user.getUsername())
				.password(user.getPassword())
				.authorities(getAuthorities(user))
				.accountExpired(!user.getAccountNonExpired())
				.accountLocked(!user.getAccountNonLocked())
				.credentialsExpired(!user.getCredentialsNonExpired())
				.disabled(!user.getEnabled())
				.build();
	}
	
	private Collection<? extends GrantedAuthority> getAuthorities(User user) {
		Set<GrantedAuthority> authorities = new java.util.HashSet<>();
		
		// Rolleri ekle
		user.getRoles().forEach(role -> {
			authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getCode()));
			// Permission'ları ekle
			role.getPermissions().forEach(permission -> {
				authorities.add(new SimpleGrantedAuthority(permission.getCode()));
			});
		});
		
		return authorities;
	}
}

