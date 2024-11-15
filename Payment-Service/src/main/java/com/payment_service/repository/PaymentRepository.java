package com.payment_service.repository;

import com.payment_service.model.Payments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface PaymentRepository extends JpaRepository<Payments,String> {
    Optional<Payments> findByBookingId(String bookingId);
}
