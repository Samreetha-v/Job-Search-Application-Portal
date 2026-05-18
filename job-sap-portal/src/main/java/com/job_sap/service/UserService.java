// 5. service/UserService.java
package com.job_sap.service;

import com.job_sap.config.JwtUtil;
import com.job_sap.dto.AuthRequest;
import com.job_sap.dto.AuthResponse;
import com.job_sap.entity.User;
import com.job_sap.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.job_sap.enums.AppRole;
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public String registerUser(AuthRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // Strictly assign the role based on input
        try {
            AppRole assignedRole = AppRole.valueOf(request.getRole().toUpperCase());
            user.setRole(assignedRole);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role. Must be ROLE_RECRUITER or ROLE_JOBSEEKER");
        }

        userRepository.save(user);
        return "User registered successfully";
    }

    public AuthResponse loginUser(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        
        // Pass the role to the token generator
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token,user);
    }
}