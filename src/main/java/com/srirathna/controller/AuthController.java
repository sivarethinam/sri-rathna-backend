package com.srirathna.controller;

import com.srirathna.dto.Dtos.*;
import com.srirathna.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<String>> sendOtp(@RequestBody RegisterRequest request) {
        ApiResponse<String> response = authService.sendOtp(
            request.getMobile(), request.getName(), request.getEmail()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(@RequestBody VerifyOtpRequest request) {
        ApiResponse<AuthResponse> response = authService.verifyOtp(request.getMobile(), request.getOtp());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin-login")
    public ResponseEntity<ApiResponse<AuthResponse>> adminLogin(@RequestBody AdminLoginRequest request) {
        ApiResponse<AuthResponse> response = authService.adminLogin(request.getMobile(), request.getPassword());
        return ResponseEntity.ok(response);
    }
}
