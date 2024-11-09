package com.train_service.serviceImpl;

import com.dto.CommonDTO.LocationDTO;
import com.dto.CommonDTO.TrainDTO;
import com.train_service.customException.TrainNotFound;
import com.train_service.model.Train;
import com.train_service.repository.TrainRepository;
import com.train_service.service.TrainService;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@AllArgsConstructor
@Data
public class TrainServiceImpl implements TrainService {


    private final TrainRepository trainRepository;
    private final WebClient client;


    public TrainServiceImpl
            (WebClient.Builder webClientBuilder,
             TrainRepository trainRepository)
    {
        this.client = webClientBuilder.baseUrl("http://LOCATION-SERVICE").build();
        this.trainRepository = trainRepository;
    }

    @Override
    public List<TrainDTO> getAll() {
        return trainRepository.findAll().stream().map(this::convertTraintoTrainDTO).toList();
    }

    @Override
    public TrainDTO getTrainByTrainNumber(String trainNumber) {
        Train train = trainRepository.findByTrainNumber(trainNumber).
                    orElseThrow(() -> new TrainNotFound(" Invalid Train Number"));
        return convertTraintoTrainDTO(train);
    }

    @Override
    public TrainDTO getTrainWithFullRoute(String trainNumber) {
      Train train = trainRepository.findByTrainNumber(trainNumber).orElseThrow(() -> new TrainNotFound("Train Not found"));
      return convertTraintoTrainDTO(train);
    }

    @Override
    public void addStationToRoute(String trainNumber, long locationId, @Min(value = 2, message = "Values should Start from 2") int order) {
        Train train = trainRepository.findByTrainNumber(trainNumber).orElseThrow(()-> new TrainNotFound("Train Not Found"));
        train.getTrainRoute().put(1,train.getSource());
        train.getTrainRoute().put(order,fetchLocationFromLocationService(locationId).getStationName());
        int max = train.getTrainRoute().keySet().stream().max(Integer::compareTo).orElse(1);
        train.getTrainRoute().put(max + 1, train.getDestination());

        trainRepository.save(train);



    }

    @Override
    public LocationDTO fetchLocationFromLocationService(long locationId) {
        return client.get()
                .uri("api/location/DTO/{locationId}",locationId)
                .retrieve().bodyToMono(LocationDTO.class)
                .block();

    }

    @Override
    public List<TrainDTO> sendDataOfTrainsForStationName(String stationName) {
        return trainRepository.findAll().stream()
                .filter(train -> train.getTrainRoute()
                        .values().stream().anyMatch(station ->
                                station.equalsIgnoreCase(stationName)))
                                        .map(this::convertTraintoTrainDTO)
                                                    .toList();

    }

    private TrainDTO convertTraintoTrainDTO(Train train) {
        return TrainDTO.builder().id(train.getId())
                .trainNumber(train.getTrainNumber())
                .source(train.getSource())
                .destination(train.getDestination())
                .name(train.getName())
                .trainRoute(train.getTrainRoute())
                .build();
    }

}
