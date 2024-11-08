package com.train_service.service.serviceImpl;

import com.train_service.customException.TrainNotFound;
import com.train_service.model.Train;
import com.train_service.model.TrainRouteStations;
import com.train_service.repository.TrainRepository;
import com.train_service.repository.TrainRouteStationRepository;
import com.train_service.service.TrainService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class TrainServiceImpl implements TrainService {

    @Autowired
    public TrainRepository trainRepository;
    @Autowired
    public TrainRouteStationRepository trainRouteStationRepository;

    @Override
    public List<Train> getAll() {
        return trainRepository.findAll();
    }

    @Override
    public Train getTrainByTrainNumber(String trainNumber) {
        return trainRepository.findByTrainNumber(trainNumber).
                orElseThrow(() -> new TrainNotFound(" Invalid Train Number"));
    }

    /* @Override
    public Train getTrainWithFullRoute(String trainNumber) {

    }*/

    @Override
    public void addStationToRoute(String trainNumber, long locationId, int order, int stoppage) {
        TrainRouteStations routeStations = new TrainRouteStations();
       Train train =  trainRepository.findByTrainNumber(trainNumber).
               orElseThrow(() -> new TrainNotFound("Train Not Found"));
        routeStations.setTrain(train);
        routeStations.setStationOrder(order);
        routeStations.setLocationId(locationId);
        routeStations.setStoppage(stoppage);
        trainRouteStationRepository.save(routeStations);

    }
}
