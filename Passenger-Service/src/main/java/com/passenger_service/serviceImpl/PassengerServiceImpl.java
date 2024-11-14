package com.passenger_service.serviceImpl;

import com.dto.CommonDTO.BookingDTO;
import com.dto.CommonDTO.PassengerDTO;
import com.passenger_service.customException.PassengerAlreadyExistsException;
import com.passenger_service.customException.PassengerNotFoundException;
import com.passenger_service.model.Passenger;
import com.passenger_service.repository.PassengerRepository;
import com.passenger_service.service.PassengerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PassengerServiceImpl implements PassengerService {


    private final PassengerRepository repository;
    private final WebClient bookingClient;

    public PassengerServiceImpl(PassengerRepository repository, WebClient.Builder bookingClient ){
        this.repository = repository ;
        this.bookingClient = bookingClient.baseUrl("lb://BOOKING-SERVICE").build();
    }
    @Override
    public ResponseEntity<Void> createPassenger(PassengerDTO passengerDTO) throws ParseException {
        if(repository.existsById(passengerDTO.getId()))
            throw new PassengerAlreadyExistsException("Passenger with id " +passengerDTO.getId()+" already exists in Database");
        repository.save(convertPassengerDTOIntoPassenger(passengerDTO));
        return ResponseEntity.ok().build();
    }

    @Override
    public PassengerDTO sendPassengerDetailsToOtherServices(String passengerId) {
        Passenger passenger = repository.findById(passengerId).orElseThrow(() ->
                new PassengerNotFoundException("Passenger with "+ passengerId +" does not exists in Database"));
        return convertPassengerIntoPassengerDTO(passenger);
    }

    private PassengerDTO convertPassengerIntoPassengerDTO(Passenger passenger) {
        return PassengerDTO.builder()
                .id(passenger.getId())
                .email(passenger.getEmail())
                .firstName(passenger.getFirstName())
                .lastName(passenger.getLastName())
                .bookings(passenger.getBookings())
                .dateOfBirth(passenger.getDateOfBirth().toString())
                .build();
    }

    @Override
    public ResponseEntity<List<BookingDTO>> getAllBookingsMadeByPassenger(long passengerId) {
        return null;
    }

    @Override
    public void deletePassenger(PassengerDTO passengerDTO) throws ParseException {
        if(!repository.existsById(passengerDTO.getId()))
            throw new PassengerNotFoundException("Passenger doesnt exists cannot delete passenger");
        repository.delete(convertPassengerDTOIntoPassenger(passengerDTO));
    }
    private Date convertDtoDatesIntoParsableDates(String dateOfBirth)
            throws ParseException{
        try{
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            return format.parse(dateOfBirth);
        } catch (ParseException e) {
            throw new RuntimeException("Invalid date format: " + dateOfBirth, e);
        }
    }
    private Passenger convertPassengerDTOIntoPassenger(PassengerDTO passengerDTO) throws ParseException {
        return Passenger.builder()
                .id(passengerDTO.getId())
                .email(passengerDTO.getEmail())
                .firstName(passengerDTO.getFirstName())
                .lastName(passengerDTO.getLastName())
                .bookings(passengerDTO.getBookings())
                .dateOfBirth(convertDtoDatesIntoParsableDates(passengerDTO.getDateOfBirth()))
                .build();
    }
}
