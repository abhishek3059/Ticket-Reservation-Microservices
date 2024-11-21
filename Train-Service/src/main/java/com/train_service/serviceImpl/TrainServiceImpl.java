package com.train_service.serviceImpl;

import com.dto.CommonDTO.LocationDTO;
import com.dto.CommonDTO.RouteDistanceDTO;
import com.dto.CommonDTO.TrainDTO;
import com.train_service.customException.TrainAlreadyExists;
import com.train_service.customException.TrainNotFound;
import com.train_service.enums.ClassType;
import com.train_service.enums.Days;
import com.train_service.enums.SeatType;
import com.train_service.model.RouteDistance;
import com.train_service.model.SeatAllocation;
import com.train_service.model.Train;
import com.train_service.repository.SeatAllocationRepository;
import com.train_service.repository.TrainRepository;
import com.train_service.service.TrainService;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class TrainServiceImpl implements TrainService {


    private final TrainRepository trainRepository;
    private final WebClient client;
    private final WebClient bookingClient;
    private final SeatAllocationRepository repository;

    public TrainServiceImpl
            (WebClient.Builder webClientBuilder,
             WebClient.Builder bookingClient,
             TrainRepository trainRepository, SeatAllocationRepository repository)
    {
        this.client = webClientBuilder.baseUrl("lb://LOCATION-SERVICE").build();
        this.bookingClient = bookingClient.baseUrl("lb://BOOKING-SERVICE").build();
        this.trainRepository = trainRepository;
        this.repository = repository;
    }


    @Override
    public ResponseEntity<List<TrainDTO>> getAll() {
        List<TrainDTO> trainDTOS = trainRepository.findAll().stream().map(this::convertTraintoTrainDTO).toList();
        return ResponseEntity.ok(trainDTOS);
    }

    @Override
    public ResponseEntity<TrainDTO> getTrainByTrainNumber(String trainNumber) {
        Train train = trainRepository.findByTrainNumber(trainNumber).
                    orElseThrow(() -> new TrainNotFound(" Invalid Train Number"));
        return ResponseEntity.ok(convertTraintoTrainDTO(train));
    }

    @Override
    public ResponseEntity<TrainDTO> getTrainWithFullRoute(String trainNumber) {
      Train train = trainRepository.findByTrainNumber(trainNumber).orElseThrow(() -> new TrainNotFound("Train Not found"));
      if(!train.getTrainRouteDistance().isEmpty()){
          return ResponseEntity.ok(convertTraintoTrainDTO(train));
      }
      else throw new
              RuntimeException("Train has no route Admin please update by using -> " +
              "type post -> uri='api/trains/update-train-route/{trainNumber}");
    }

    @Override
    public void addStationToRoute(String trainNumber, Map<Integer, Long> stationWithOrder) {
        Train train = trainRepository.findByTrainNumber(trainNumber)
                .orElseThrow(() -> new TrainNotFound("Train with train number " + trainNumber + " does not exist"));

        if (train.getTrainRouteDistance().isEmpty()) {
            train.setTrainRouteDistance(new LinkedHashMap<>());
        }

        // Add source station
        RouteDistance sourceStation = new RouteDistance();
        sourceStation.setStationName(train.getSource());
        sourceStation.setDistanceFromStart(0.0);
        train.getTrainRouteDistance().put(1, sourceStation);

        Double combinedDistance = 0.0;

        // Add intermediate stations
        for (Map.Entry<Integer, Long> entry : stationWithOrder.entrySet()) {
            Integer order = entry.getKey();
            Long locationId = entry.getValue();

            LocationDTO station = fetchLocationFromLocationService(locationId);
            if (station == null) {
                throw new IllegalArgumentException("Station cannot be found in location-service");
            }

            if (order > 1) {
                // Get the previous station
                RouteDistance previousStation = train.getTrainRouteDistance().get(order - 1);
                if (previousStation == null) {
                    throw new IllegalStateException("Previous station not found for order: " + (order - 1));
                }

                // Calculate the distance from the previous station to the current station
                Double distanceBetweenStations = calculateDistanceBetweenStation(
                        previousStation.getStationName(),
                        station.getStationName()
                );
                combinedDistance += distanceBetweenStations;
            }

            // Add the current station to the route
            RouteDistance routeDistance = new RouteDistance();
            routeDistance.setStationName(station.getStationName());
            routeDistance.setDistanceFromStart(combinedDistance);
            train.getTrainRouteDistance().put(order, routeDistance);
        }

        // Add destination station after processing all intermediate stations
        int maxOrder = train.getTrainRouteDistance().keySet().stream().max(Integer::compareTo).orElse(1);
        RouteDistance lastStation = train.getTrainRouteDistance().get(maxOrder);
        if (lastStation == null) {
            throw new IllegalStateException("Last station not found for order: " + maxOrder);
        }

        Double finalDistance = calculateDistanceBetweenStation(lastStation.getStationName(), train.getDestination());
        RouteDistance destinationStation = new RouteDistance();
        destinationStation.setStationName(train.getDestination());
        destinationStation.setDistanceFromStart(combinedDistance + finalDistance);
        train.getTrainRouteDistance().put(maxOrder + 1, destinationStation);

        // Save the train entity
        trainRepository.save(train);
    }


    @Override
    public ResponseEntity<Double> getTotalDistanceForTrain(String trainNumber){
        Train train = trainRepository.findByTrainNumber(trainNumber).orElseThrow(()->
                new TrainNotFound("Train with "+trainNumber+" does not exists"));
        Double distance = train.getTrainRouteDistance()
                .values()
                .stream().map(RouteDistance::getDistanceFromStart)
                .max(Double::compareTo)
                .orElse(100.0);
        return ResponseEntity.ok(distance);
    }
    @Override
    public ResponseEntity<Double> getTotalDistanceBetweenBoardingAndDestination(String trainNumber, String boardingStation, String destination){
        Train train = trainRepository.findByTrainNumber(trainNumber).orElseThrow(()->
                new TrainNotFound("Train with "+trainNumber+" does not exists"));
        Double max = null;
        Double boardingStationDistance = null;
        Double destinationDistance = null;
      for(RouteDistance distance : train.getTrainRouteDistance().values()){
          if(distance.getStationName().equals(boardingStation)){
              boardingStationDistance = distance.getDistanceFromStart();
          }
          if(distance.getStationName().equals(destination)){
              destinationDistance = distance.getDistanceFromStart();
          }
      }
      if(boardingStationDistance != null && destinationDistance != null){
          max = Math.abs(destinationDistance - boardingStationDistance);
      }
      else{
          throw new IllegalArgumentException("Stations "+ boardingStation+" and "+destination+" should be in route");
      }
      return ResponseEntity.ok(max);


    }

    @Override
    public LocationDTO fetchLocationFromLocationService(long locationId) {
        return client.get()
                .uri("api/location/DTO/{locationId}",locationId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,response ->{
                    return Mono.error(new IllegalArgumentException("Invalid location id :"+ locationId));
                })
                .onStatus(HttpStatusCode::is5xxServerError, response ->{
                    return Mono.error(new RuntimeException("Location Service server is not responding"));
                })
                .bodyToMono(LocationDTO.class)
                .block();


    }

    @Override
    public List<String> sendDataOfTrainsForStationName(String stationName) {
       List<TrainDTO> trainsByStations =  trainRepository.findAll().stream().filter(train -> train.getTrainRouteDistance() != null
                       && train.getTrainRouteDistance()
                       .values().stream()
                       .anyMatch(routeDistance ->
                               routeDistance.getStationName() != null &&
                                       routeDistance.getStationName()
                                               .equalsIgnoreCase(stationName)))
               .map(this::convertTraintoTrainDTO).toList();
      return trainsByStations.stream().map(TrainDTO::getTrainNumber).toList();
    }

    @Override
    public void addTrain(TrainDTO train) {
        if(trainRepository.existsByTrainNumber(train.getTrainNumber())){
            throw new TrainAlreadyExists("Train Already exists in database");
        }
        Train trainToBeSaved = convertTrainDTOtoTrain(train);
        trainRepository.save(trainToBeSaved);
        initializeSeats(trainToBeSaved.getTrainNumber());
        trainRepository.save(trainToBeSaved);


    }
    @Override
    public ResponseEntity<List<Integer>> getAvailableSeats(String trainNumber, String bookingClass, String seatChoice){
       Train train = trainRepository.findByTrainNumber(trainNumber).orElseThrow(() -> new TrainNotFound("Train Not found"));
       ClassType classType = ClassType.valueOf(bookingClass);
       SeatType seatType = SeatType.valueOf(seatChoice);
        List<SeatAllocation> seats = repository.findAllByClassType(classType);

        if (seats != null && !seats.isEmpty()) {
            // Filter seats based on SeatType, train and availability (not booked)
            List<Integer> availableSeats = seats.stream()
                    .filter(seat -> seat.getSeatType().equals(seatType)
                            && seat.getTrain().equals(train)
                            && !seat.getIsBooked())  // Check if seat is not booked
                    .map(SeatAllocation::getSeatNumber)  // Extract seat numbers
                    .collect(Collectors.toList());  // Collect available seat numbers into a list

            // Return available seats as a response
            if (availableSeats.isEmpty()) {
                return ResponseEntity.noContent().build(); // No seats available
            } else {
                return ResponseEntity.ok(availableSeats);  // Return available seats
            }
        }

        // Return bad request if no seats found
        return ResponseEntity.badRequest().body(Collections.emptyList());

    }
    @Override
    public ResponseEntity<Boolean> bookSeat(
            String trainNumber,
            String bookingClassType,
            String bookingSeatType,
            Integer seatNumber
    ) {
        // Convert strings to enums
        ClassType classType = ClassType.valueOf(bookingClassType.toUpperCase());
        SeatType seatType = SeatType.valueOf(bookingSeatType.toUpperCase());

        // Fetch the train by trainNumber
        Train train = trainRepository.findByTrainNumber(trainNumber)
                .orElseThrow(() -> new TrainNotFound("Train with "+  trainNumber + " does not exist"));

        // Fetch all seat allocations by classType and train
        List<SeatAllocation> seats = repository.findAllByClassTypeAndTrain(classType, train);

        // Iterate through the seat list
        for (SeatAllocation seat : seats) {
            // Check for seat type, seat number, and availability
            if (seat.getSeatType() == seatType &&
                    seat.getSeatNumber().equals(seatNumber) &&
                    !seat.getIsBooked()) {
                seat.setIsBooked(true);
                repository.save(seat);
                // Persist changes to the database
                seat.setBookingId(fetchBookingIdFromBookingService(seat.getSeatNumber()));
                repository.save(seat);
                return ResponseEntity.ok(true); // Seat booking successful
            }
        }

        // No suitable seat found
        return ResponseEntity.badRequest().body(false);
    }

    private String fetchBookingIdFromBookingService(Integer seatNumber) {
        return bookingClient.get().uri(uri -> uri.path("/api/bookings/bookingIdAfterSaving")
                .queryParam("seatNumber",seatNumber)
                .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }


    @Override
    public ResponseEntity<Boolean> releaseSeat(String trainNumber, String bookingId) {
        // Retrieve the train object based on train number
        Train train = trainRepository.findByTrainNumber(trainNumber).orElseThrow(() ->
                new TrainNotFound("Train with trainNumber " + trainNumber + " cannot be found"));

        // Find the booked seat associated with the bookingId (returns Optional)
        Optional<SeatAllocation> bookedSeatOptional = repository.findByBookingId(bookingId);

        // Check if a seat with the given bookingId is found
        if (bookedSeatOptional.isPresent()) {
            SeatAllocation seat = bookedSeatOptional.get();

            // Ensure the seat belongs to the given train and is currently booked
            if (seat.getTrain().equals(train) && seat.getIsBooked()) {
                // Release the seat (unbook it)
                seat.setIsBooked(false);
                seat.setBookingId(null);  // Clear the booking ID
                repository.save(seat);  // Save updated seat
                return ResponseEntity.ok(true);  // Successfully released the seat
            }
        }
        return ResponseEntity.badRequest().build();
    }

    private void initializeSeats(String trainNumber) {
        Train train = trainRepository.findByTrainNumber(trainNumber)
                .orElseThrow(() ->
                        new TrainNotFound("Train Not exists"));


        Map<ClassType , Integer> seatMap = new HashMap<>();
        seatMap.put(ClassType.FIRST_AC, 50);
        seatMap.put(ClassType.AC_TWO_TIER, 75);
        seatMap.put(ClassType.AC_THREE_TIER,100);
        seatMap.put(ClassType.SLEEPER, 125);

        for(ClassType classType : ClassType.values()){
            int seatCount = seatMap.get(classType);
            for(int i = 1; i <= seatCount; i++){
                for(SeatType seatType : SeatType.values()){
                    SeatAllocation seat = new SeatAllocation();
                    seat.setSeatNumber(i);
                    seat.setTrain(train);
                    seat.setClassType(classType);
                    seat.setBookingId(null);
                    seat.setIsBooked(false);
                }
            }
        }
        train.setSeatConfiguration(seatMap);
        trainRepository.save(train);

    }

    private TrainDTO convertTraintoTrainDTO(Train train) {
        TrainDTO dto = TrainDTO.builder()
                .trainNumber(train.getTrainNumber())
                .source(train.getSource())
                .destination(train.getDestination())
                .name(train.getName())
                .build();
        Map<Integer,RouteDistanceDTO> convertedMap = convertTrainRouteDistanceIntoTrainRouteDistanceDTO(train.getTrainRouteDistance());
        dto.setTrainRouteDistance(convertedMap);
        return dto;
    }

    private Map<Integer, RouteDistanceDTO> convertTrainRouteDistanceIntoTrainRouteDistanceDTO(Map<Integer, RouteDistance> trainRouteDistance) {
        Map<Integer,RouteDistanceDTO> toBeConvertedMap = new LinkedHashMap<>();
        for (Map.Entry<Integer,RouteDistance> entry : trainRouteDistance.entrySet()){
            toBeConvertedMap.put(entry.getKey(), convertRouteDistanceIntoRouteDistanceDTO(entry.getValue()));
        }
        return toBeConvertedMap;
    }

    private RouteDistanceDTO convertRouteDistanceIntoRouteDistanceDTO(RouteDistance value) {
        RouteDistanceDTO dto = new RouteDistanceDTO();
        dto.setStationName(value.getStationName());
        dto.setDistanceFromStart(value.getDistanceFromStart());
        return dto;
    }
    private Map<Integer, RouteDistance> convertTrainRouteDistanceDTOIntoTrainRouteDistance(Map<Integer, RouteDistanceDTO> trainRouteDistance) {
        if(trainRouteDistance == null){
            return new LinkedHashMap<>();
        }
        Map<Integer,RouteDistance> toBeConvertedMap = new LinkedHashMap<>();
        for (Map.Entry<Integer,RouteDistanceDTO> entry : trainRouteDistance.entrySet()){
            toBeConvertedMap.put(entry.getKey(), convertRouteDistanceDTOIntoRouteDistance(entry.getValue()));
        }
        return toBeConvertedMap;
    }

    private RouteDistance convertRouteDistanceDTOIntoRouteDistance(RouteDistanceDTO value) {
        RouteDistance distance = new RouteDistance();
        distance.setStationName(value.getStationName());
        distance.setDistanceFromStart(value.getDistanceFromStart());
        return distance;
    }


    private Train convertTrainDTOtoTrain(TrainDTO train){
         Train train1 = Train.builder()
                 .trainNumber(train.getTrainNumber())
                 .source(train.getSource())
                 .destination(train.getDestination())
                 .name(train.getName())
                 .trainRouteDistance(convertTrainRouteDistanceDTOIntoTrainRouteDistance(train.getTrainRouteDistance()))
                 .build();
        Set<Days> convertedDTODays = convertDTORunningDaysIntoTrainRunningDays(train.getRunningDays());
        if (train.getRunningDays() != null && checkValidDaysForSavingRunningDaysOfTrain(convertedDTODays)) {
            train.setRunningDays(train.getRunningDays());
        } else {
            throw new IllegalArgumentException("Invalid running days provided.");
        }
         return train1;
     }

    private Set<Days> convertDTORunningDaysIntoTrainRunningDays(Set<String> runningDays) {
        Set<Days> days = EnumSet.noneOf(Days.class);
        for(String S : runningDays){
            days.add(Days.valueOf(S.toUpperCase()));
        }
        return days;
    }

    private boolean checkValidDaysForSavingRunningDaysOfTrain(Set<Days> runningDays) {
        // Validate that all days in the set are part of the Days enum
        return runningDays.stream().allMatch(day -> Arrays.asList(Days.values()).contains(day));
    }

    private Double calculateDistanceBetweenStation(String source, String destination) {
        try {
            Double distance = client.get()
                    .uri("/api/location/DTO/distance/{sourceStationName}/{destinationStationName}",source,destination)
                    .retrieve()
                    .bodyToMono(Double.class)
                    .block(); // Blocks until response is received
            return distance;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching distance between " + source + " and " + destination, e);
        }
    }



}
