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
import com.train_service.repository.TrainRepository;
import com.train_service.service.TrainService;
import org.springframework.http.HttpStatusCode;
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

    public TrainServiceImpl
            (WebClient.Builder webClientBuilder,
             TrainRepository trainRepository)
    {
        this.client = webClientBuilder.baseUrl("lb://LOCATION-SERVICE").build();
        this.trainRepository = trainRepository;
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
    public void addStationToRoute(String trainNumber, Map<Integer,Long> stationWithOrder) {
       Train train = trainRepository.findByTrainNumber(trainNumber)
               .orElseThrow(()-> new TrainNotFound("Train with train number "+trainNumber+ " does not exists"));
       if(train.getTrainRouteDistance().isEmpty()){
           train.setTrainRouteDistance(new LinkedHashMap<>());
       }
        RouteDistance sourceStation = new RouteDistance();
       sourceStation.setStationName(train.getSource());
       sourceStation.setDistanceFromStart(0.0);
       train.getTrainRouteDistance().put(1,sourceStation);

       Double combinedDistance = 0.0;

       for(Map.Entry<Integer,Long> entry : stationWithOrder.entrySet()){
           Integer order = entry.getKey();
           Long locationId = entry.getValue();
           LocationDTO station = fetchLocationFromLocationService(locationId);
           if(station == null){
               throw new IllegalArgumentException("Station cannot be found in location-service");

           }
           if(order > 1){
               Double distanceBetweenStations = calculateDistanceBetweenStation(train.getTrainRouteDistance().get(order - 1).getStationName(),station.getStationName());
               combinedDistance += distanceBetweenStations;
           }
           RouteDistance routeDistance = new RouteDistance();
           routeDistance.setStationName(station.getStationName());
           routeDistance.setDistanceFromStart(combinedDistance);
           train.getTrainRouteDistance().put(order,routeDistance);

           int max = train.getTrainRouteDistance().keySet().stream().max(Integer::compareTo).orElse(1);
           Double finalDistance = calculateDistanceBetweenStation(train.getTrainRouteDistance().get(max).getStationName(),train.getDestination());
           RouteDistance destinationStation = new RouteDistance();
           destinationStation.setStationName(train.getDestination());
           destinationStation.setDistanceFromStart(combinedDistance+finalDistance);
           train.getTrainRouteDistance().put(max + 1, destinationStation);

           trainRepository.save(train);
       }

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
        initializeSeats(trainToBeSaved);
        trainRepository.save(trainToBeSaved);


    }
    @Override
    public ResponseEntity<List<Integer>> getAvailableSeats(String trainNumber, String bookingClass, String seatChoice){

        ClassType classType = ClassType.valueOf(bookingClass.toUpperCase());
        SeatType seatType = SeatType.valueOf(seatChoice);
        Train train = trainRepository.findByTrainNumber(trainNumber).orElseThrow(() ->
                new TrainNotFound("Train with " + trainNumber+ " does not exists"));
      List<Integer> seatList =  train.getSeatAllocations()
              .get(classType)
              .get(seatType)
              .entrySet()
              .stream()
              .filter(entry ->
                      !entry.getValue().getIsBooked())
              .map(Map.Entry::getKey)
              .toList();
      return ResponseEntity.ok(seatList);
    }
    @Override
    public ResponseEntity<Boolean> bookSeat
            (String trainNumber, String bookingClassType,String bookingSeatType, Integer seatNumber, String bookingId){
        ClassType classType = ClassType.valueOf(bookingClassType);
        SeatType seatType = SeatType.valueOf(bookingSeatType);

        Train train = trainRepository.findByTrainNumber(trainNumber).orElseThrow(() ->
                new TrainNotFound("Train with " + trainNumber+ " does not exists"));

        Map<Integer,SeatAllocation> seatMap = train.getSeatAllocations().get(classType).get(seatType);
        SeatAllocation seat = seatMap.get(seatNumber);
        if(seat != null && !seat.getIsBooked()){
            seat.setIsBooked(true);
            seat.setBookingId(bookingId);
            trainRepository.save(train);
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.ok(false);

    }
    @Override
    public void releaseSeat(String trainNumber, String bookingId){
       Train train = trainRepository.findByTrainNumber(trainNumber).orElseThrow(() ->
                new TrainNotFound("Train with trainNumber "+trainNumber+" cannot be found"));
        for(Map.Entry<ClassType,Map<SeatType,Map<Integer,SeatAllocation>>> entry1 : train.getSeatAllocations().entrySet()){
            Map<SeatType,Map<Integer,SeatAllocation>> innerMap1 = entry1.getValue();
            for(Map.Entry<SeatType,Map<Integer,SeatAllocation>> entry2 : innerMap1.entrySet()){
                Map<Integer,SeatAllocation> innerMap2 = entry2.getValue();
                for(Map.Entry<Integer,SeatAllocation> entry3 : innerMap2.entrySet()){
                    SeatAllocation seat = entry3.getValue();
                    if(seat.getBookingId().equals(bookingId)){
                        seat.setBookingId(null);
                        seat.setIsBooked(false);
                    }
                }
            }

        }
        trainRepository.save(train);
    }

    private void initializeSeats(Train train) {
      Map<ClassType,Map<SeatType,Map<Integer, SeatAllocation>>> allSeatMap = new HashMap<>();
      for(ClassType classType : ClassType.values()){
      Map<SeatType,Map<Integer,SeatAllocation>> seatTypeAllocationMap = new HashMap<>();
        for(SeatType seatType : SeatType.values()){
            Map<Integer,SeatAllocation>seatAllocationMap = new HashMap<>();
           int seatCount = switch (classType){
                case FIRST_AC -> 15;
                case AC_TWO_TIER -> 25;
                case AC_THREE_TIER -> 45;
                case SLEEPER -> 75;
            };
           for(int i = 1; i <= seatCount; i++){
               SeatAllocation seat = new SeatAllocation();
               seat.setSeatNumber(i);
               seat.setIsBooked(false);
               seat.setBookingId(null);
               seatAllocationMap.put(i,seat);
           }
           seatTypeAllocationMap.put(seatType,seatAllocationMap);
        }
        allSeatMap.put(classType, seatTypeAllocationMap);
      }
      train.setSeatAllocations(allSeatMap);

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
         if (train.getRunningDays() != null && checkValidDaysForSavingRunningDaysOfTrain(train.getRunningDays())) {
             EnumSet<Days> runningDays = convertDTORunningDaysIntoTrainRunningDays(train.getRunningDays());
             train1.setRunningDays(runningDays);
         }
         return train1;
     }

    private EnumSet<Days> convertDTORunningDaysIntoTrainRunningDays(Set<String> runningDays) {
        EnumSet<Days> days = EnumSet.noneOf(Days.class);
        for(String S : runningDays){
            days.add(Days.valueOf(S.toUpperCase()));
        }
        return days;
    }

    private Boolean checkValidDaysForSavingRunningDaysOfTrain(Set<String> runningDays) {
             boolean flag = true;
         for(String days : runningDays){
             if(!(days.equalsIgnoreCase("monday")||
                     days.equalsIgnoreCase("tuesday")||
                     days.equalsIgnoreCase("wednesday")||
                     days.equalsIgnoreCase("thursday")||
                     days.equalsIgnoreCase("friday")||
                     days.equalsIgnoreCase("saturday")||
                     days.equalsIgnoreCase("sunday"))){
                 flag = false;
                 throw new IllegalArgumentException("Invalid day "+ days);
             }

         }
         return flag;
    }

    private Double calculateDistanceBetweenStation(String source, String destination) {
            return client.get().uri("/DTO/distance/{sourceStationName}/{destinationStationName}",source,destination)
                    .retrieve().bodyToMono(Double.class).block();
        }



}
