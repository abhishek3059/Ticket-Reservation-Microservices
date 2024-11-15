package com.locationSevice.serviceImpl;

import com.dto.CommonDTO.StationDistanceDTO;
import com.locationSevice.customException.StationNotFound;
import com.locationSevice.model.Location;
import com.locationSevice.model.StationDistance;
import com.locationSevice.repository.DistanceRepository;
import com.locationSevice.repository.LocationRepository;
import com.locationSevice.service.DistanceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

@RequiredArgsConstructor
public class DistanceServiceImpl implements DistanceService {
    private final LocationRepository locationRepository;
    private final DistanceRepository distanceRepository;
    @Override
    public ResponseEntity<Double> getDistance(String sourceStationName, String destinationStationName) {
       Double distance = distanceRepository.findBySource_StationNameAndDistance_StationName(sourceStationName,destinationStationName)
               .map(StationDistance::getDistance).orElseThrow(() ->
                       new StationNotFound("distance between stations cannot be found"));
       return ResponseEntity.ok(distance);
    }

    @Override
    @Transactional
    public ResponseEntity<StationDistanceDTO> setDistanceBetweenStations(String source, String destination, Double distance) {
        Location sourceStation = locationRepository.findByStationName(source).orElseThrow(() ->
                new StationNotFound("Station "+source+" does not exists"));
        Location destinationStation = locationRepository.findByStationName(destination).orElseThrow(() ->
                new StationNotFound("Station "+destination+" does not exists"));
        StationDistance stationDistance = StationDistance.builder()
                .destination(destinationStation)
                .source(sourceStation)
                .distance(distance).build();
        distanceRepository.save(stationDistance);
        return ResponseEntity.ok(convertStationDistanceToStationDistanceDTO(stationDistance));
    }

    private StationDistanceDTO convertStationDistanceToStationDistanceDTO(StationDistance stationDistance) {
       return StationDistanceDTO.builder()
                .destinationStationName(stationDistance.getDestination().getStationCode())
               .sourceStationName(stationDistance.getSource().getStationCode())
               .distance(stationDistance.getDistance()).build();
    }

}
