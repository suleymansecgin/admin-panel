package com.suleymansecgin.admin_panel.service;

import com.suleymansecgin.admin_panel.dto.AuthRequest;
import com.suleymansecgin.admin_panel.dto.AuthResponse;
import com.suleymansecgin.admin_panel.dto.RegisterRequest;
import com.suleymansecgin.admin_panel.exception.BaseException;
import com.suleymansecgin.admin_panel.exception.ErrorMessage;
import com.suleymansecgin.admin_panel.exception.MessageType;
import com.suleymansecgin.admin_panel.jwt.JwtTokenProvider;
import com.suleymansecgin.admin_panel.model.User;
import com.suleymansecgin.admin_panel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BaseException(new ErrorMessage(MessageType.USER_ALREADY_EXISTS, 
                    "Kullanıcı adı: " + request.getUsername()));
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BaseException(new ErrorMessage(MessageType.EMAIL_ALREADY_EXISTS, 
                    "E-posta: " + request.getEmail()));
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole(request.getRole() != null ? request.getRole() : "USER");
        
        user = userRepository.save(user);
        
        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getRole());
        
        return new AuthResponse(
                token,
                "Bearer",
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }
    
    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.INVALID_CREDENTIALS, null)));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BaseException(new ErrorMessage(MessageType.INVALID_CREDENTIALS, null));
        }
        
        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getRole());
        
        return new AuthResponse(
                token,
                "Bearer",
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }
}

