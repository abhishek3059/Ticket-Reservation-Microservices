package com.train_service.service;

import com.train_service.model.Train;
import com.train_service.model.TrainRouteStations;

import java.util.List;

public interface TrainService {

    public List<Train> getAll();
    public Train getTrainByTrainNumber(String trainNumber);
   // public Train getTrainWithFullRoute(String trainNumber);
    public void addStationToRoute(String trainNumber, long locationId, int order, int stoppage);
}
