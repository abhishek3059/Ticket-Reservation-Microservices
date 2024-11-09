package com.train_service.service;

import com.dto.CommonDTO.LocationDTO;
import com.dto.CommonDTO.TrainDTO;

import java.util.List;

public interface TrainService {

    public List<TrainDTO> getAll();
    public TrainDTO getTrainByTrainNumber(String trainNumber);
    public TrainDTO getTrainWithFullRoute(String trainNumber);
    public void addStationToRoute
            (String trainNumber, long locationId, int order);
    public LocationDTO fetchLocationFromLocationService(long locationId);
    public List<TrainDTO> sendDataOfTrainsForStationName(String stationName);

}
