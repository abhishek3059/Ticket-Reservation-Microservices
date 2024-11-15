package com.dto.CommonDTO;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PaymentResponse {
    private String bookingId;
    private String paymentId;
    private String paymentStatus;
    private Double amount;
    private LocalDate timestamp;

}
