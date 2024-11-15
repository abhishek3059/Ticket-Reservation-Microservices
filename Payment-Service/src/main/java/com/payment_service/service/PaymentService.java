package com.payment_service.service;

import com.dto.CommonDTO.PaymentRequest;
import com.dto.CommonDTO.PaymentResponse;

public interface PaymentService {

    PaymentResponse initiatePayment(PaymentRequest request);
    PaymentResponse getPaymentDetails(String bookingId);
    PaymentResponse processRefund(String bookingId);
}
