package com.srirathna.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Dtos {

    public static class RegisterRequest {
        private String name;
        private String mobile;
        private String email;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getMobile() { return mobile; }
        public void setMobile(String mobile) { this.mobile = mobile; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class OtpRequest {
        private String mobile;
        public String getMobile() { return mobile; }
        public void setMobile(String mobile) { this.mobile = mobile; }
    }

    public static class VerifyOtpRequest {
        private String mobile;
        private String otp;

        public String getMobile() { return mobile; }
        public void setMobile(String mobile) { this.mobile = mobile; }

        public String getOtp() { return otp; }
        public void setOtp(String otp) { this.otp = otp; }
    }

    public static class AuthResponse {
        private String token;
        private String role;
        private String name;
        private String mobile;
        private UUID userId;

        public AuthResponse() {}

        public AuthResponse(String token, String role, String name, String mobile, UUID userId) {
            this.token = token;
            this.role = role;
            this.name = name;
            this.mobile = mobile;
            this.userId = userId;
        }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getMobile() { return mobile; }
        public void setMobile(String mobile) { this.mobile = mobile; }

        public UUID getUserId() { return userId; }
        public void setUserId(UUID userId) { this.userId = userId; }
    }

    public static class BookingRequest {
        private String customerName;
        private String mobile;
        private String email;
        private String address;
        private String functionDate;
        private String functionType;
        private String paymentReference;
        private String notes;
        private UUID userId;

        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }

        public String getMobile() { return mobile; }
        public void setMobile(String mobile) { this.mobile = mobile; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public String getFunctionDate() { return functionDate; }
        public void setFunctionDate(String functionDate) { this.functionDate = functionDate; }

        public String getFunctionType() { return functionType; }
        public void setFunctionType(String functionType) { this.functionType = functionType; }

        public String getPaymentReference() { return paymentReference; }
        public void setPaymentReference(String paymentReference) { this.paymentReference = paymentReference; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }

        public UUID getUserId() { return userId; }
        public void setUserId(UUID userId) { this.userId = userId; }
    }

    public static class BookingResponse {
        private UUID id;
        private String customerName;
        private String mobile;
        private String email;
        private String address;
        private LocalDate functionDate;
        private String functionType;
        private BigDecimal totalAmount;
        private BigDecimal advanceAmount;
        private BigDecimal remainingAmount;
        private String paymentStatus;
        private String paymentReference;
        private String bookingStatus;
        private Boolean bookedByAdmin;
        private String notes;
        private LocalDateTime createdAt;
        private UUID userId;

        public BookingResponse() {}

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }

        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }

        public String getMobile() { return mobile; }
        public void setMobile(String mobile) { this.mobile = mobile; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public LocalDate getFunctionDate() { return functionDate; }
        public void setFunctionDate(LocalDate functionDate) { this.functionDate = functionDate; }

        public String getFunctionType() { return functionType; }
        public void setFunctionType(String functionType) { this.functionType = functionType; }

        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

        public BigDecimal getAdvanceAmount() { return advanceAmount; }
        public void setAdvanceAmount(BigDecimal advanceAmount) { this.advanceAmount = advanceAmount; }

        public BigDecimal getRemainingAmount() { return remainingAmount; }
        public void setRemainingAmount(BigDecimal remainingAmount) { this.remainingAmount = remainingAmount; }

        public String getPaymentStatus() { return paymentStatus; }
        public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

        public String getPaymentReference() { return paymentReference; }
        public void setPaymentReference(String paymentReference) { this.paymentReference = paymentReference; }

        public String getBookingStatus() { return bookingStatus; }
        public void setBookingStatus(String bookingStatus) { this.bookingStatus = bookingStatus; }

        public Boolean getBookedByAdmin() { return bookedByAdmin; }
        public void setBookedByAdmin(Boolean bookedByAdmin) { this.bookedByAdmin = bookedByAdmin; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

        public UUID getUserId() { return userId; }
        public void setUserId(UUID userId) { this.userId = userId; }
    }

    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;

        public ApiResponse() {}

        public ApiResponse(boolean success, String message, T data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        public static <T> ApiResponse<T> ok(String message, T data) {
            return new ApiResponse<>(true, message, data);
        }

        public static <T> ApiResponse<T> error(String message) {
            return new ApiResponse<>(false, message, null);
        }

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
    }

    public static class AdminLoginRequest {
        private String mobile;
        private String password;

        public String getMobile() { return mobile; }
        public void setMobile(String mobile) { this.mobile = mobile; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}