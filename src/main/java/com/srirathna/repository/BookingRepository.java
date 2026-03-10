package com.srirathna.repository;

import com.srirathna.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    Optional<Booking> findByFunctionDate(LocalDate functionDate);
    boolean existsByFunctionDate(LocalDate functionDate);
    List<Booking> findByUserId(UUID userId);

    @Query("SELECT b.functionDate FROM Booking b WHERE b.bookingStatus = 'confirmed'")
    List<LocalDate> findAllBookedDates();

    List<Booking> findAllByOrderByCreatedAtDesc();
}
