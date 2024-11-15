package com.passenger_service.service;

import com.dto.CommonDTO.BookingDTO;
import com.dto.CommonDTO.PassengerDTO;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.List;

public interface PassengerService {
    public ResponseEntity<Void> createPassenger(PassengerDTO passengerDTO) throws ParseException;
    public PassengerDTO sendPassengerDetailsToOtherServices(String passengerId);

    ResponseEntity<Boolean> verifyPassenger(String passengerId);

    public ResponseEntity<List<BookingDTO>> getAllBookingsMadeByPassenger(long passengerId);
    public void deletePassenger(PassengerDTO passengerDTO) throws ParseException;

}
