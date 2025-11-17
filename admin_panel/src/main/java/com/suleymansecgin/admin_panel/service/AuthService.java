package com.suleymansecgin.admin_panel.service;

import com.suleymansecgin.admin_panel.dto.*;
import com.suleymansecgin.admin_panel.exception.BaseException;
import com.suleymansecgin.admin_panel.exception.ErrorMessage;
import com.suleymansecgin.admin_panel.exception.MessageType;
import com.suleymansecgin.admin_panel.jwt.JwtService;
import com.suleymansecgin.admin_panel.model.RefreshToken;
import com.suleymansecgin.admin_panel.model.User;
import com.suleymansecgin.admin_panel.repository.RefreshTokenRepository;
import com.suleymansecgin.admin_panel.repository.RoleRepository;
import com.suleymansecgin.admin_panel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private CustomUserDetailsService userDetailsService;
	
	@Autowired
	private MfaService mfaService;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Value("${jwt.expiration:86400000}")
	private Long expiration;
	
	@Transactional
	public AuthResponse login(LoginRequest request) {
		User user = userRepository.findByUsername(request.getUsername())
				.orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.INVALID_CREDENTIALS, null)));
		
		// MFA kontrolü
		if (user.getMfaEnabled()) {
			if (request.getMfaCode() == null || request.getMfaCode().isEmpty()) {
				AuthResponse response = new AuthResponse();
				response.setMfaRequired(true);
				return response;
			}
			
			if (!mfaService.verifyCode(request.getMfaCode(), user.getMfaSecret())) {
				throw new BaseException(new ErrorMessage(MessageType.INVALID_MFA_CODE, null));
			}
		}
		
		// Kimlik doğrulama
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						request.getUsername(),
						request.getPassword()
				)
		);
		
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		
		// Token oluştur
		String accessToken = jwtService.generateToken(userDetails);
		String refreshToken = jwtService.generateRefreshToken(userDetails.getUsername());
		
		// Refresh token'ı kaydet
		saveRefreshToken(user, refreshToken);
		
		// Son giriş zamanını güncelle
		user.setLastLoginAt(LocalDateTime.now());
		userRepository.save(user);
		
		// Kullanıcı bilgilerini yükle
		User fullUser = userRepository.findByUsernameWithRolesAndPermissions(user.getUsername())
				.orElse(user);
		
		return buildAuthResponse(accessToken, refreshToken, fullUser);
	}
	
	@Transactional
	public AuthResponse register(RegisterRequest request) {
		if (userRepository.existsByUsername(request.getUsername())) {
			throw new BaseException(new ErrorMessage(MessageType.USERNAME_ALREADY_EXISTS, null));
		}
		
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new BaseException(new ErrorMessage(MessageType.EMAIL_ALREADY_EXISTS, null));
		}
		
		User user = new User();
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		
		// İlk kullanıcı otomatik aktif ve ADMIN rolü alır, diğerleri onay bekliyor
		boolean isFirstUser = userRepository.count() == 0;
		user.setEnabled(isFirstUser);
		user.setAccountNonExpired(true);
		user.setAccountNonLocked(true);
		user.setCredentialsNonExpired(true);
		user.setMfaEnabled(false);
		
		// İlk kullanıcıya otomatik olarak ADMIN rolü ver
		if (isFirstUser) {
			roleRepository.findByCode("ADMIN").ifPresent(user.getRoles()::add);
		}
		// Sonraki kullanıcılar için rol atanmaz, yönetici tarafından atanacak
		
		User savedUser = userRepository.save(user);
		
		// İlk kullanıcı ise token oluştur ve login yap, değilse sadece kullanıcı bilgilerini döndür
		if (isFirstUser) {
			UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getUsername());
			String accessToken = jwtService.generateToken(userDetails);
			String refreshToken = jwtService.generateRefreshToken(savedUser.getUsername());
			
			saveRefreshToken(savedUser, refreshToken);
			
			User fullUser = userRepository.findByUsernameWithRolesAndPermissions(savedUser.getUsername())
					.orElse(savedUser);
			
			return buildAuthResponse(accessToken, refreshToken, fullUser);
		} else {
			// Onay bekleyen kullanıcı için token oluşturma, sadece kullanıcı bilgilerini döndür
			AuthResponse response = new AuthResponse();
			response.setAccessToken(null);
			response.setRefreshToken(null);
			response.setExpiresIn(null);
			
			AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo();
			userInfo.setId(savedUser.getId());
			userInfo.setUsername(savedUser.getUsername());
			userInfo.setEmail(savedUser.getEmail());
			userInfo.setFirstName(savedUser.getFirstName());
			userInfo.setLastName(savedUser.getLastName());
			userInfo.setEnabled(savedUser.getEnabled());
			userInfo.setMfaEnabled(savedUser.getMfaEnabled());
			userInfo.setRoles(new java.util.HashSet<>());
			userInfo.setPermissions(new java.util.HashSet<>());
			
			response.setUser(userInfo);
			return response;
		}
	}
	
	@Transactional
	public AuthResponse refreshToken(RefreshTokenRequest request) {
		RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
				.orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.REFRESH_TOKEN_NOT_FOUND, null)));
		
		if (refreshToken.getRevoked()) {
			throw new BaseException(new ErrorMessage(MessageType.REFRESH_TOKEN_REVOKED, null));
		}
		
		if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
			throw new BaseException(new ErrorMessage(MessageType.REFRESH_TOKEN_EXPIRED, null));
		}
		
		User user = refreshToken.getUser();
		UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
		
		String newAccessToken = jwtService.generateToken(userDetails);
		String newRefreshToken = jwtService.generateRefreshToken(user.getUsername());
		
		// Eski refresh token'ı iptal et
		refreshToken.setRevoked(true);
		refreshTokenRepository.save(refreshToken);
		
		// Yeni refresh token'ı kaydet
		saveRefreshToken(user, newRefreshToken);
		
		User fullUser = userRepository.findByUsernameWithRolesAndPermissions(user.getUsername())
				.orElse(user);
		
		return buildAuthResponse(newAccessToken, newRefreshToken, fullUser);
	}
	
	@Transactional
	public void logout(String refreshToken) {
		refreshTokenRepository.findByToken(refreshToken)
				.ifPresent(token -> {
					token.setRevoked(true);
					refreshTokenRepository.save(token);
				});
	}
	
	@Transactional
	public MfaSetupResponse setupMfa(String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, null)));
		
		if (user.getMfaEnabled()) {
			throw new BaseException(new ErrorMessage(MessageType.MFA_ALREADY_ENABLED, null));
		}
		
		String secret = mfaService.generateSecret();
		String qrCodeUrl = mfaService.generateQrCodeImageUri(secret, user.getEmail());
		
		MfaSetupResponse response = new MfaSetupResponse();
		response.setSecret(secret);
		response.setQrCodeUrl(qrCodeUrl);
		response.setManualEntryKey(secret);
		
		return response;
	}
	
	@Transactional
	public void enableMfa(String username, String secret, String code) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, null)));
		
		if (!mfaService.verifyCode(code, secret)) {
			throw new BaseException(new ErrorMessage(MessageType.INVALID_MFA_CODE, null));
		}
		
		user.setMfaSecret(secret);
		user.setMfaEnabled(true);
		userRepository.save(user);
	}
	
	@Transactional
	public void disableMfa(String username, String code) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, null)));
		
		if (!user.getMfaEnabled()) {
			throw new BaseException(new ErrorMessage(MessageType.MFA_NOT_ENABLED, null));
		}
		
		if (!mfaService.verifyCode(code, user.getMfaSecret())) {
			throw new BaseException(new ErrorMessage(MessageType.INVALID_MFA_CODE, null));
		}
		
		user.setMfaEnabled(false);
		user.setMfaSecret(null);
		userRepository.save(user);
	}
	
	private void saveRefreshToken(User user, String token) {
		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setToken(token);
		refreshToken.setUser(user);
		refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));
		refreshToken.setRevoked(false);
		refreshTokenRepository.save(refreshToken);
	}
	
	private AuthResponse buildAuthResponse(String accessToken, String refreshToken, User user) {
		AuthResponse response = new AuthResponse();
		response.setAccessToken(accessToken);
		response.setRefreshToken(refreshToken);
		response.setExpiresIn(expiration / 1000); // saniye cinsinden
		
		AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo();
		userInfo.setId(user.getId());
		userInfo.setUsername(user.getUsername());
		userInfo.setEmail(user.getEmail());
		userInfo.setFirstName(user.getFirstName());
		userInfo.setLastName(user.getLastName());
		userInfo.setEnabled(user.getEnabled());
		userInfo.setMfaEnabled(user.getMfaEnabled());
		
		Set<String> roles = user.getRoles().stream()
				.map(role -> role.getCode())
				.collect(Collectors.toSet());
		userInfo.setRoles(roles);
		
		Set<String> permissions = user.getRoles().stream()
				.flatMap(role -> role.getPermissions().stream())
				.map(permission -> permission.getCode())
				.collect(Collectors.toSet());
		userInfo.setPermissions(permissions);
		
		response.setUser(userInfo);
		return response;
	}
}

