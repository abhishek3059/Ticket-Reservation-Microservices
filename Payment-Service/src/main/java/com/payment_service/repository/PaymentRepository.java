package com.payment_service.repository;

import com.payment_service.model.Payments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payments,String> {
}
