package com.srirathna.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

// ---- Auth DTOs ----

@Data
class RegisterRequest {
    private String name;
    private String mobile;
    private String email;
}

@Data
class OtpRequest {
    private String mobile;
}

@Data
class VerifyOtpRequest {
    private String mobile;
    private String otp;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class AuthResponse {
    private String token;
    private String role;
    private String name;
    private String mobile;
    private UUID userId;
}
