package com.booking_service.service;

import com.dto.CommonDTO.BookingDTO;
import com.dto.CommonDTO.PassengerDTO;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;

public interface BookingService {

    public ResponseEntity<Void> createBooking(PassengerDTO passengerDTO, String dateOfBooking);
    public List<BookingDTO> getBookingsByPassengerId(String passengerId);
    public List<BookingDTO> getAllBookings();
    public ResponseEntity<Void> deleteBooking(String passengerId, String bookingId);

}
