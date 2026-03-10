package com.srirathna.controller;

import com.srirathna.dto.Dtos.*;
import com.srirathna.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // Public endpoint - get all booked dates
    @GetMapping("/booked-dates")
    public ResponseEntity<ApiResponse<List<LocalDate>>> getBookedDates() {
        return ResponseEntity.ok(bookingService.getBookedDates());
    }

    // Create booking (user or admin)
    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @RequestBody BookingRequest request,
            Authentication authentication) {
        String mobile = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        return ResponseEntity.ok(bookingService.createBooking(request, mobile, isAdmin));
    }

    // Get my bookings (user)
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getMyBookings(Authentication authentication) {
        return ResponseEntity.ok(bookingService.getUserBookings(authentication.getName()));
    }

    // Get booking by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(@PathVariable UUID id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    // Admin: get all bookings
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getAllBookings(Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        if (!isAdmin) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }
        return ResponseEntity.ok(bookingService.getAllBookings());
    }
}
