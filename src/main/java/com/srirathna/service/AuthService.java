package com.srirathna.service;

import com.srirathna.config.JwtUtil;
import com.srirathna.dto.Dtos.*;
import com.srirathna.model.OtpLog;
import com.srirathna.model.User;
import com.srirathna.repository.OtpLogRepository;
import com.srirathna.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpLogRepository otpLogRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${app.otp.expiry-minutes:10}")
    private int otpExpiryMinutes;

    public ApiResponse<String> sendOtp(String mobile, String name, String email) {
        User user;

        if (userRepository.existsByMobile(mobile)) {
            user = userRepository.findByMobile(mobile).get();
            if (name != null && !name.isBlank()) user.setName(name);
            if (email != null && !email.isBlank()) user.setEmail(email);
        } else {
            if (name == null || name.isBlank()) {
                return ApiResponse.error("Name is required for new registration");
            }
            user = new User();
            user.setMobile(mobile);
            user.setName(name);
            user.setEmail(email);
            user.setRole("user");
        }

        String otp = String.format("%06d", new Random().nextInt(999999));
        user.setOtp(otp);
        user.setOtpExpiresAt(LocalDateTime.now().plusMinutes(otpExpiryMinutes));
        userRepository.save(user);

        OtpLog otpLog = new OtpLog();
        otpLog.setMobile(mobile);
        otpLog.setOtp(otp);
        otpLog.setPurpose("login");
        otpLogRepository.save(otpLog);

        System.out.println("=== OTP for " + mobile + ": " + otp + " ===");

        return ApiResponse.ok("OTP sent successfully to " + mobile, "OTP: " + otp + " (demo mode)");
    }

    public ApiResponse<AuthResponse> verifyOtp(String mobile, String otpInput) {
        User user = userRepository.findByMobile(mobile).orElse(null);

        if (user == null) {
            return ApiResponse.error("Mobile number not registered");
        }

        if (user.getOtp() == null || !user.getOtp().equals(otpInput)) {
            return ApiResponse.error("Invalid OTP");
        }

        if (user.getOtpExpiresAt() == null || LocalDateTime.now().isAfter(user.getOtpExpiresAt())) {
            return ApiResponse.error("OTP has expired. Please request a new one.");
        }

        user.setIsVerified(true);
        user.setOtp(null);
        user.setOtpExpiresAt(null);
        userRepository.save(user);

        otpLogRepository.findByMobileOrderBySentAtDesc(mobile)
                .stream().findFirst().ifPresent(log -> {
                    log.setIsUsed(true);
                    log.setVerifiedAt(LocalDateTime.now());
                    otpLogRepository.save(log);
                });

        // Generate token
        String token = jwtUtil.generateToken(mobile, user.getRole(), user.getName());
        System.out.println("=== Generated token for " + mobile + ": " + token.substring(0, 20) + "... ===");

        // Build response manually to avoid any Lombok issues
        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(token);
        authResponse.setRole(user.getRole());
        authResponse.setName(user.getName());
        authResponse.setMobile(mobile);
        authResponse.setUserId(user.getId());

        System.out.println("=== AuthResponse token: " + authResponse.getToken() + " ===");

        return ApiResponse.ok("Login successful", authResponse);
    }

    public ApiResponse<AuthResponse> adminLogin(String mobile, String password) {
        User user = userRepository.findByMobile(mobile).orElse(null);

        if (user == null || !"admin".equals(user.getRole())) {
            return ApiResponse.error("Invalid admin credentials");
        }

        if (!"admin123".equals(password)) {
            return ApiResponse.error("Invalid password");
        }

        String token = jwtUtil.generateToken(mobile, user.getRole(), user.getName());

        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(token);
        authResponse.setRole(user.getRole());
        authResponse.setName(user.getName());
        authResponse.setMobile(mobile);
        authResponse.setUserId(user.getId());

        return ApiResponse.ok("Admin login successful", authResponse);
    }
}