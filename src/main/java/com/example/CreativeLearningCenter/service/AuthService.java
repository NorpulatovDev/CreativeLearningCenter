package com.example.CreativeLearningCenter.service;

import com.example.CreativeLearningCenter.config.JwtTokenProvider;
import com.example.CreativeLearningCenter.dto.AuthRequest;
import com.example.CreativeLearningCenter.dto.AuthResponse;
import com.example.CreativeLearningCenter.entity.Admin;
import com.example.CreativeLearningCenter.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    
    public AuthResponse login(AuthRequest request) {
        Admin admin = adminRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
        
        if (!admin.getActive()) {
            throw new IllegalArgumentException("Account is disabled");
        }
        
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        
        String token = tokenProvider.generateToken(admin.getUsername());
        log.info("User logged in: {}", admin.getUsername());
        
        return new AuthResponse(token, admin.getUsername());
    }
    
    public void createDefaultAdmin() {
        if (!adminRepository.existsByUsername("admin")) {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123")); // Change in production!
            admin.setActive(true);
            adminRepository.save(admin);
            log.info("Default admin created: username=admin, password=admin123");
        }
    }
}