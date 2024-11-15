package com.dto.CommonDTO;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PaymentRequest {
    private String bookingId;
    private String passengerId;
    private Double amount;
    private String trainNumber;
    private String classType;
    private String seatType;
    private String  paymentMethod;
    private String status;
    private double tax;

}
