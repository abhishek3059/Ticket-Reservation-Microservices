package com.booking_service.controller;

import com.booking_service.service.BookingService;
import com.dto.CommonDTO.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/bookings")
public class BookingController {

    private final BookingService bookingService;
    @PostMapping("/create") @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> createBooking(@RequestBody BookingCreationRequest request){
        PassengerDTO passengerDTO = request.getPassengerDTO();
        BookingRequest bookingDTO = request.getBookingDTO();
        return bookingService.createBooking(passengerDTO,bookingDTO);

    }
    @GetMapping("/{passengerId}")@ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<BookingDTO>> getBookingsByPassengerId(@PathVariable String passengerId){
        List<BookingDTO> bookings = bookingService.getBookingsByPassengerId(passengerId);
        return ResponseEntity.ok(bookings);
    }
    @GetMapping("/all")@ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<BookingDTO>> getAllBookings(){
        List<BookingDTO> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }
    @DeleteMapping("/delete")@ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> deleteBooking(@RequestParam String passengerId, @RequestParam String bookingId){
        return bookingService.deleteBooking(passengerId,bookingId);
    }
    @GetMapping("/payments")@ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaymentResponse> getPaymentDetails(String bookingId){
        PaymentResponse response = bookingService.getPaymentDetails(bookingId);
        return ResponseEntity.ok(response);
    }

}
