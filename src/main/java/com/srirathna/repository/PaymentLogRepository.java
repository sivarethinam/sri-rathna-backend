package com.srirathna.repository;

import com.srirathna.model.PaymentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentLogRepository extends JpaRepository<PaymentLog, UUID> {
    List<PaymentLog> findByBookingId(UUID bookingId);
}
