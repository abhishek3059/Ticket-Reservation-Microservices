package com.booking_service.service.serviceImpl;

import com.booking_service.service.BookingService;
import com.dto.CommonDTO.BookingDTO;
import com.dto.CommonDTO.PassengerDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;


public class BookingServiceImpl implements BookingService {
    @Override
    public ResponseEntity<Void> createBooking(PassengerDTO passengerDTO) {
        return null;
    }

    @Override
    public List<BookingDTO> getBookingsByPassengerId(String passengerId) {
        return List.of();
    }

    @Override
    public List<BookingDTO> getAllBookings() {
        return List.of();
    }

    @Override
    public ResponseEntity<Void> deleteBooking(String passengerId, String bookingId) {
        return null;
    }
}
