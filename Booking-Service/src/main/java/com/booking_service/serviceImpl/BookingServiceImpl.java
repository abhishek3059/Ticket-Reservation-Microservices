package com.booking_service.serviceImpl;

import com.booking_service.customException.SeatNotAvailable;
import com.booking_service.model.Booking;
import com.booking_service.repository.BookingRepository;
import com.booking_service.service.BookingService;
import com.dto.CommonDTO.*;
import jakarta.transaction.Transactional;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final WebClient trainClient;
    private final BookingRepository repository;
    private final WebClient passengerClient;
    private final WebClient locationClient;
    private final WebClient paymentClient;


    public BookingServiceImpl(WebClient.Builder trainClient,WebClient.Builder passengerClient,WebClient.Builder locationClient,WebClient.Builder paymentClient, BookingRepository repository ){
        this.trainClient = trainClient.baseUrl("lb://TRAIN-SERVICE").build();
        this.passengerClient = passengerClient.baseUrl("lb://PASSENGER-SERVICE").build();
        this.locationClient = locationClient.baseUrl("lb://LOCATION-SERVICE").build();
        this.paymentClient = paymentClient.baseUrl("lb://PAYMENT-SERVICE").build();
        this.repository = repository;
    }

    @Override
    @Transactional
    public ResponseEntity<Void> createBooking(PassengerDTO passengerDTO, BookingRequest request) {
     if(!verifyPassengerDetails(passengerDTO.getId())){
         throw new IllegalArgumentException("Passenger details not valid");
     }
     if(!validateTrainDetails(request.getTrainNumber())){
         throw new IllegalArgumentException("Enter valid train Details");
     }
     if(!verifyStationDetails(request.getBoardingStation()) || !verifyStationDetails(request.getDestination())){
         throw new IllegalArgumentException("Enter valid station details");
     }
     if(!getIsSeatAvailable(request)){
         throw new SeatNotAvailable("Seat is not available try another seat");
     }
     else{
         Double totalAmount = calculateFare(request);
         PaymentRequest paymentRequest = PaymentRequest.builder()
                 .paymentMethod("CREDIT_CARD")
                 .tax(0.5)
                 .amount(totalAmount)
                 .classType(request.getClassType())
                 .seatType(request.getSeatType())
                 .trainNumber(request.getTrainNumber())
                 .passengerId(passengerDTO.getId())
                 .build();
         PaymentResponse response = initiatePayment(paymentRequest);
         if(response.getPaymentStatus().equals("COMPLETED")){
             Booking booking = Booking.builder()
                     .date(LocalDate.now())
                     .boardingStation(request.getBoardingStation())
                     .destinationStation(request.getDestination())
                     .trainName(getTrainDetails(request.getTrainNumber()))
                     .ticketPrice(response.getAmount())
                     .paymentId(response.getPaymentId())
                     .passengerId(passengerDTO.getId())
                     .build();
             repository.save(booking);
         }
     }

    return ResponseEntity.ok().build();
    }

    @Override
    public List<BookingDTO> getBookingsByPassengerId(String passengerId) {
       List<Booking> filteredList = repository
               .findAll()
               .stream()
               .filter(bookings ->
                       bookings.getPassengerId().equals(passengerId))
               .toList();
       return filteredList
               .stream()
               .map(this::convertBookingIntoBookingDTO)
               .toList();

    }



    @Override
    public List<BookingDTO> getAllBookings() {
        return repository.findAll()
                .stream()
                .map(this::convertBookingIntoBookingDTO)
                .toList();
    }

    @Override
    @Transactional
    public ResponseEntity<Void> deleteBooking(String passengerId, String bookingId) {
        Booking booking = repository.findById(bookingId).orElseThrow(() ->
             new IllegalArgumentException("Booking Not Available"));
        if (!verifyPassengerDetails(passengerId)) {
            throw new IllegalArgumentException("Enter valid passenger details");
        }
        PaymentResponse response = initiateRefund(bookingId);
        if(response != null && response.getPaymentStatus().equals("REFUNDED")){
            CancelRequest request = new CancelRequest();
            request.setBookingId(bookingId);
            request.setTrainNumber(booking.getTrainNumber());
            releaseSeat(request);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @Override
    public PaymentResponse getPaymentDetails(String bookingId){
        return paymentClient.get()
                .uri("api/payments/{bookingId}",bookingId)
                .retrieve()
                .bodyToMono(PaymentResponse.class)
                .block();
    }

    private BookingDTO convertBookingIntoBookingDTO(Booking dto){
        return BookingDTO.builder()
                .passengerId(dto.getPassengerId())
                .bookingId(dto.getBookingId())
                .trainNumber(dto.getTrainNumber())
                .trainName(dto.getTrainName())
                .ticketPrice(dto.getTicketPrice())
                .boardingStation(dto.getBoardingStation())
                .destinationStation(dto.getDestinationStation())
                .date(dto.getDate())
                .build();
    }
    private Double getTotalDistanceBetweenPassengersSourceAndDistance(BookingRequest request){
        return trainClient.get()
                .uri(uri -> uri
                        .path("api/trains/trainDTO/get-distance-between-stations")
                        .queryParam("trainNumber",request.getTrainNumber())
                        .queryParam("boardingStation",request.getBoardingStation())
                        .queryParam("destination",request.getDestination())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Double.class)
                .block();
}
    private Boolean getIsSeatAvailable(BookingRequest request){
      return  trainClient.put().uri(uri -> uri
                .path("api/trains/trainDTO/Seat-Availability")
                .queryParam("trainNumber",request.getTrainNumber())
                .queryParam("classType",request.getClassType())
                .queryParam("seatType",request.getSeatType())
                .queryParam("seatNumber",request.getSeatNumber())
                .queryParam("bookingId",request.getBookingId())
                .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
    }
    private void releaseSeat(CancelRequest request){
         trainClient.put().uri(uri -> uri
                .path("/trainDTO/cancel-booking")
                .queryParam("trainNumber",request.getTrainNumber())
                .queryParam("bookingId",request.getBookingId())
                .build())
                .retrieve()
                 .toBodilessEntity()
                 .block();

    }
    private Boolean verifyPassengerDetails(String passengerId){
        return passengerClient.get().uri(uri -> uri
                .path("/api/passenger/passengerDTO/verify")
                .queryParam("passengerId",passengerId)
                .build())
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

    }
    private Boolean verifyStationDetails(String stationName){
        return locationClient.get()
                .uri(uri -> uri
                        .path("api/location/DTO/verify")
                        .queryParam("stationName",stationName)
                        .build())
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
    }
    private PaymentResponse initiatePayment(PaymentRequest request){
        return paymentClient.post()
                .uri("api/payments/process-payment")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PaymentResponse.class)
                .block();
    }
    private PaymentResponse initiateRefund(String bookingId){
        return paymentClient.put()
                .uri(uri -> uri
                        .path("api/payments/process-refund")
                        .queryParam("bookingId",bookingId)
                        .build())
                .retrieve()
                .bodyToMono(PaymentResponse.class)
                .block();
    }
    private Boolean validateTrainDetails(String trainNumber){
        TrainDTO trainDTO = trainClient.get()
                .uri("api/trains/public/{trainNumber}",trainNumber)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(TrainDTO.class)
                .block();
        return trainDTO == null;
    }
    private Double calculateFare(BookingRequest request){
        final Double BASE_FARE = 100.0;
        final double BASE_TAX = 0.5;
        Double distance = getTotalDistanceBetweenPassengersSourceAndDistance(request);
        return (BASE_FARE * distance) * BASE_TAX;
    }
    private String getTrainDetails(String trainNumber){
        TrainDTO trainDTO = trainClient.get()
                .uri("api/trains/public/{trainNumber}",trainNumber)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(TrainDTO.class)
                .block();
        if(trainDTO != null)
        return trainDTO.getTrainNumber();
        else throw new IllegalArgumentException("Train Not found");
    }


}

