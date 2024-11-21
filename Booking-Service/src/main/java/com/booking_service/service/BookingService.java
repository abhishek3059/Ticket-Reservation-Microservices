package com.booking_service.service;

import com.booking_service.model.Booking;
import com.dto.CommonDTO.*;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;

public interface BookingService {

    public ResponseEntity<Void> createBooking(PassengerDTO passengerDTO, BookingRequest request);



    public List<BookingDTO> getBookingsByPassengerId(String passengerId);
    public List<BookingDTO> getAllBookings();
    public ResponseEntity<Void> deleteBooking(String passengerId, String bookingId);

    PaymentResponse getPaymentDetails(String bookingId);

    ResponseEntity<String> sendBookingIdAfterBooking(Integer seatNumber);
}
