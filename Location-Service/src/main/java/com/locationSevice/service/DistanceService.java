package com.locationSevice.service;

import com.dto.CommonDTO.StationDistanceDTO;
import org.springframework.http.ResponseEntity;

public interface DistanceService {

    public ResponseEntity<Double> getDistance(String sourceStationCode, String destinationStationCode);
    public ResponseEntity<StationDistanceDTO> setDistanceBetweenStations(String source, String destination, Double distance);
}
