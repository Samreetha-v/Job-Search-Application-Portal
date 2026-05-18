package com.job_sap.controller;

import com.job_sap.config.JwtUtil;
import com.job_sap.dto.AuthRequest;
import com.job_sap.dto.AuthResponse;
import com.job_sap.entity.User;
import com.job_sap.enums.AppRole; // <-- FIX 1: Added missing import here
import com.job_sap.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // AppRole will now resolve correctly because of the import above
        user.setRole(AppRole.valueOf(request.getRole().toUpperCase())); 
        
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name()); 
            
            // Pass BOTH the token and the user details to the frontend
            return ResponseEntity.ok(new AuthResponse(token, user));
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }
}