package com.booking_service.serviceImpl;

import com.booking_service.repository.BookingRepository;
import com.booking_service.service.BookingService;
import com.dto.CommonDTO.BookingDTO;
import com.dto.CommonDTO.PassengerDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;


public class BookingServiceImpl implements BookingService {

    private final WebClient trainClient;
    private final BookingRepository repository;
    private final WebClient passengerClient;
    private final WebClient locationClient;


    public BookingServiceImpl(WebClient.Builder trainClient,WebClient.Builder passengerClient,WebClient.Builder locationClient, BookingRepository repository ){
        this.trainClient = trainClient.baseUrl("lb://TRAIN-SERVICE").build();
        this.passengerClient = trainClient.baseUrl("lb://PASSENGER-SERVICE").build();
        this.locationClient = trainClient.baseUrl("lb://LOCATION-SERVICE").build();
        this.repository = repository;
    }

    @Override
    public ResponseEntity<Void> createBooking(PassengerDTO passengerDTO, String dateOfBooking) {
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
