package com.payment_service.model;

import com.payment_service.enums.PaymentMethod;
import com.payment_service.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class Payments {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String bookingId;
    private String passengerId;
    private String trainNumber;
    @Column(nullable =false)
    private Double tax;
    @Column(nullable = false)
    private Double totalAmount;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    @Enumerated(EnumType.STRING)
    private PaymentMethod method;
    private LocalDate initiatedAt;
    private LocalDate completedAt;
}
