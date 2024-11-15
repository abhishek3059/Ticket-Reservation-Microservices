package com.payment_service.serviceImpl;

import com.dto.CommonDTO.PaymentRequest;
import com.dto.CommonDTO.PaymentResponse;
import com.payment_service.enums.PaymentMethod;
import com.payment_service.enums.PaymentStatus;
import com.payment_service.model.Payments;
import com.payment_service.repository.PaymentRepository;
import com.payment_service.service.PaymentService;
import jakarta.ws.rs.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
@AllArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public PaymentResponse initiatePayment(PaymentRequest request) {
       Payments payment =  Payments.builder()
               .method(PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase()))
               .status(PaymentStatus.PROCESSING)
               .passengerId(request.getPassengerId())
               .trainNumber(request.getTrainNumber())
               .tax(request.getTax())
               .totalAmount(request.getAmount())
               .initiatedAt(LocalDate.now())
               .build();

        paymentRepository.save(payment);
        if(Math.random() > 0.2){
            payment.setStatus(PaymentStatus.COMPLETED);
        }
        else{
            payment.setStatus(PaymentStatus.FAILED);
        }
        payment.setCompletedAt(LocalDate.now());
        paymentRepository.save(payment);
        return convertPaymentToPaymentResponse(payment);
    }

    private PaymentResponse convertPaymentToPaymentResponse(Payments payment) {
        return PaymentResponse.builder()
                .bookingId(payment.getBookingId())
                .paymentId(payment.getId())
                .amount(payment.getTotalAmount())
                .paymentStatus(payment.getStatus().name())
                .timestamp(payment.getCompletedAt() != null ? payment.getCompletedAt() : payment.getInitiatedAt())
                .build();
    }

    @Override
    public PaymentResponse getPaymentDetails(String bookingId) {
        Payments payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new NotFoundException("Payment with "+bookingId+" doesnt exists"));
        return convertPaymentToPaymentResponse(payment);
    }

    @Override
    public PaymentResponse processRefund(String bookingId) {
        Payments payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Payment with "+bookingId+" does not exists"));
        if(payment.getStatus() != PaymentStatus.COMPLETED){
            throw new IllegalArgumentException("Only payments with completed status are allowed");
        }
        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setCompletedAt(LocalDate.now());
        paymentRepository.save(payment);
        return convertPaymentToPaymentResponse(payment);


    }
}
