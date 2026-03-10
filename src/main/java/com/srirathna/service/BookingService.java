package com.srirathna.service;

import com.srirathna.dto.Dtos.*;
import com.srirathna.model.Booking;
import com.srirathna.model.PaymentLog;
import com.srirathna.model.User;
import com.srirathna.repository.BookingRepository;
import com.srirathna.repository.PaymentLogRepository;
import com.srirathna.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentLogRepository paymentLogRepository;

    private static final Map<String, BigDecimal> FUNCTION_PRICES = Map.of(
        "ear_ceremony", BigDecimal.valueOf(10000),
        "marriage", BigDecimal.valueOf(20000),
        "birthday_party", BigDecimal.valueOf(8000)
    );

    private static final BigDecimal ADVANCE_AMOUNT = BigDecimal.valueOf(1000);

    public ApiResponse<BookingResponse> createBooking(BookingRequest request, String mobile, boolean isAdmin) {
        LocalDate functionDate;
        try {
            functionDate = LocalDate.parse(request.getFunctionDate(), DateTimeFormatter.ISO_DATE);
        } catch (Exception e) {
            return ApiResponse.error("Invalid date format. Use yyyy-MM-dd");
        }

        if (bookingRepository.existsByFunctionDate(functionDate)) {
            return ApiResponse.error("This date is already booked. Please select another date.");
        }

        if (!FUNCTION_PRICES.containsKey(request.getFunctionType())) {
            return ApiResponse.error("Invalid function type. Choose: ear_ceremony, marriage, or birthday_party");
        }

        // Get user
        User user = null;
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId()).orElse(null);
        }
        if (user == null) {
            user = userRepository.findByMobile(mobile).orElse(null);
        }

        BigDecimal totalAmount = FUNCTION_PRICES.get(request.getFunctionType());
        BigDecimal remainingAmount = totalAmount.subtract(ADVANCE_AMOUNT);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setCustomerName(request.getCustomerName());
        booking.setMobile(request.getMobile() != null ? request.getMobile() : mobile);
        booking.setEmail(request.getEmail());
        booking.setAddress(request.getAddress());
        booking.setFunctionDate(functionDate);
        booking.setFunctionType(request.getFunctionType());
        booking.setTotalAmount(totalAmount);
        booking.setAdvanceAmount(ADVANCE_AMOUNT);
        booking.setRemainingAmount(remainingAmount);
        booking.setPaymentStatus("advance_paid");
        booking.setPaymentReference(request.getPaymentReference());
        booking.setBookingStatus("confirmed");
        booking.setBookedByAdmin(isAdmin);
        booking.setNotes(request.getNotes());

        Booking saved = bookingRepository.save(booking);

        // Log payment
        PaymentLog paymentLog = new PaymentLog();
        paymentLog.setBooking(saved);
        paymentLog.setAmount(ADVANCE_AMOUNT);
        paymentLog.setPaymentType("advance");
        paymentLog.setPaymentMethod("razorpay");
        paymentLog.setTransactionId(request.getPaymentReference());
        paymentLog.setStatus("success");
        paymentLogRepository.save(paymentLog);

        return ApiResponse.ok("Booking confirmed successfully!", toResponse(saved));
    }

    public ApiResponse<List<LocalDate>> getBookedDates() {
        List<LocalDate> dates = bookingRepository.findAllBookedDates();
        return ApiResponse.ok("Booked dates fetched", dates);
    }

    public ApiResponse<List<BookingResponse>> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAllByOrderByCreatedAtDesc();
        return ApiResponse.ok("Bookings fetched", bookings.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    public ApiResponse<List<BookingResponse>> getUserBookings(String mobile) {
        User user = userRepository.findByMobile(mobile).orElse(null);
        if (user == null) return ApiResponse.ok("No bookings found", List.of());
        List<Booking> bookings = bookingRepository.findByUserId(user.getId());
        return ApiResponse.ok("Bookings fetched", bookings.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    public ApiResponse<BookingResponse> getBookingById(UUID id) {
        return bookingRepository.findById(id)
                .map(b -> ApiResponse.ok("Booking found", toResponse(b)))
                .orElse(ApiResponse.error("Booking not found"));
    }

    private BookingResponse toResponse(Booking b) {
        BookingResponse r = new BookingResponse();
        r.setId(b.getId());
        r.setCustomerName(b.getCustomerName());
        r.setMobile(b.getMobile());
        r.setEmail(b.getEmail());
        r.setAddress(b.getAddress());
        r.setFunctionDate(b.getFunctionDate());
        r.setFunctionType(b.getFunctionType());
        r.setTotalAmount(b.getTotalAmount());
        r.setAdvanceAmount(b.getAdvanceAmount());
        r.setRemainingAmount(b.getRemainingAmount());
        r.setPaymentStatus(b.getPaymentStatus());
        r.setPaymentReference(b.getPaymentReference());
        r.setBookingStatus(b.getBookingStatus());
        r.setBookedByAdmin(b.getBookedByAdmin());
        r.setNotes(b.getNotes());
        r.setCreatedAt(b.getCreatedAt());
        if (b.getUser() != null) r.setUserId(b.getUser().getId());
        return r;
    }
}
