package com.payment_service.controller;

import com.dto.CommonDTO.PaymentRequest;
import com.dto.CommonDTO.PaymentResponse;
import com.payment_service.service.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process-payment")
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody PaymentRequest request){
        PaymentResponse response = paymentService.initiatePayment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<PaymentResponse> getPaymentDetails(@PathVariable String bookingId){
        PaymentResponse response = paymentService.getPaymentDetails(bookingId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/process-refund")
    public ResponseEntity<PaymentResponse> processRefund(@RequestParam String bookingId){
        PaymentResponse response = paymentService.processRefund(bookingId);
        return ResponseEntity.ok(response);
    }

}
