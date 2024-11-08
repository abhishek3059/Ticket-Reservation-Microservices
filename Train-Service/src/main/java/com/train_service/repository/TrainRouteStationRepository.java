package com.train_service.repository;

import com.train_service.model.Train;
import com.train_service.model.TrainRouteStations;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainRouteStationRepository extends JpaRepository<TrainRouteStations,Long> {
    List<TrainRouteStations> findByTrain_TrainNumberOrderByStationOrder(String trainNumber);
}
