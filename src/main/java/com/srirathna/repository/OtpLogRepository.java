package com.srirathna.repository;

import com.srirathna.model.OtpLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface OtpLogRepository extends JpaRepository<OtpLog, UUID> {
    List<OtpLog> findByMobileOrderBySentAtDesc(String mobile);
}
