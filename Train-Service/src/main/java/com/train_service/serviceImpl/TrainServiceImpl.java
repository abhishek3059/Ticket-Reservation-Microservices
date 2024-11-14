package com.train_service.serviceImpl;

import com.dto.CommonDTO.LocationDTO;
import com.dto.CommonDTO.TrainDTO;
import com.train_service.customException.TrainAlreadyExists;
import com.train_service.customException.TrainNotFound;
import com.train_service.enums.Days;
import com.train_service.enums.SeatType;
import com.train_service.model.Train;
import com.train_service.repository.TrainRepository;
import com.train_service.service.TrainService;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;


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
      if(!train.getTrainRoute().isEmpty()){
          return ResponseEntity.ok(convertTraintoTrainDTO(train));
      }
      else throw new
              RuntimeException("Train has no route Admin please update by using -> " +
              "type post -> uri='api/trains/update-train-route/{trainNumber}");
    }

    @Override
    public void addStationToRoute(String trainNumber, Map<Integer,Long> stationWithOrder) {
       Train train = trainRepository.findByTrainNumber(trainNumber)
               .orElseThrow(()-> new TrainNotFound("Train with train number "+trainNumber+ " dpes not exists"));
       if(train.getTrainRoute().isEmpty()){
           train.setTrainRoute(new HashMap<>());
       }
       train.getTrainRoute().put(1,train.getSource());

       for(Map.Entry<Integer,Long> entry : stationWithOrder.entrySet()){
           Integer order = entry.getKey();
           Long locationId = entry.getValue();
           LocationDTO station = fetchLocationFromLocationService(locationId);
           if(station == null){
               throw new IllegalArgumentException("Station with location id is not available in location service");
           }

       if(train.getTrainRoute().containsKey(order)){
           throw new IllegalArgumentException("Station with order "+ order+ " is already assigned in the database" );
       }
       train.getTrainRoute().put(order,station.getStationName());

       }

       int max = train.getTrainRoute().keySet().stream().max(Integer::compareTo).orElse(1);
       train.getTrainRoute().put(max, train.getDestination());
       trainRepository.save(train);




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
        List<TrainDTO> trainDTOs = trainRepository.findAll().stream()
                .filter(train -> train.getTrainRoute() != null &&
                        train.getTrainRoute().values().stream()
                                .anyMatch(station -> station != null &&
                                        station.equalsIgnoreCase(stationName)))
                .map(this::convertTraintoTrainDTO)
                .toList();
        return trainDTOs.stream().map(TrainDTO::getTrainNumber).toList();


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

    private void initializeSeats(Train train) {
        Map<SeatType , Integer> seatMap = new EnumMap<>(SeatType.class);
       for(SeatType seatType : SeatType.values()){
           seatMap.put(seatType,20);
       }
       train.setAvailableSeats(seatMap);

    }

    private TrainDTO convertTraintoTrainDTO(Train train) {
        return TrainDTO.builder()
                .trainNumber(train.getTrainNumber())
                .source(train.getSource())
                .destination(train.getDestination())
                .name(train.getName())
                .trainRoute(train.getTrainRoute())
                .build();
    }


     private Train convertTrainDTOtoTrain(TrainDTO train){
         Train train1 = Train.builder()
                 .trainNumber(train.getTrainNumber())
                 .source(train.getSource())
                 .destination(train.getDestination())
                 .name(train.getName())
                 .trainRoute(train.getTrainRoute()).build();
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

}
