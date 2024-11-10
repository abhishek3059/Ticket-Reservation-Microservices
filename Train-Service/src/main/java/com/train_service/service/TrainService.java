package com.train_service.service;

import com.dto.CommonDTO.LocationDTO;
import com.dto.CommonDTO.TrainDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface TrainService {

    public ResponseEntity<List<TrainDTO>> getAll();
    public ResponseEntity<TrainDTO> getTrainByTrainNumber(String trainNumber);
    public ResponseEntity<TrainDTO> getTrainWithFullRoute(String trainNumber);
    public void addStationToRoute
            (String trainNumber, Map<Integer,Long> stationWithOrder);
    public LocationDTO fetchLocationFromLocationService(long locationId);
    public List<String> sendDataOfTrainsForStationName(String stationName);
    public void addTrain (TrainDTO train);
}
