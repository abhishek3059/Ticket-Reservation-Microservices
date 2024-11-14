package com.locationSevice.service;

import com.dto.CommonDTO.StationDistanceDTO;

public interface DistanceService {

    public Double getDistance(String sourceStationCode, String destinationStationCode);
    public StationDistanceDTO setDistanceBetweenStations(String source, String destination, Double distance);
}
