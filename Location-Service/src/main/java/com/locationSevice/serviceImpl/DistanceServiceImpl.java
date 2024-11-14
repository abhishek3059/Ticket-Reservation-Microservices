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
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;

@RequiredArgsConstructor
public class DistanceServiceImpl implements DistanceService {
    private final LocationRepository locationRepository;
    private final DistanceRepository distanceRepository;
    @Override
    public Double getDistance(String sourceStationCode, String destinationStationCode) {
       return distanceRepository.findBySourceStationCodeAndDistanceStationCode(sourceStationCode,destinationStationCode)
               .map(StationDistance::getDistance).orElseThrow(() ->
                       new StationNotFound("distance between stations cannot be found"));
    }

    @Override
    @Transactional
    public StationDistanceDTO setDistanceBetweenStations(String source, String destination, Double distance) {
        Location sourceStation = locationRepository.findByStationCode(source).orElseThrow(() ->
                new StationNotFound("Station "+source+" does not exists"));
        Location destinationStation = locationRepository.findByStationCode(destination).orElseThrow(() ->
                new StationNotFound("Station "+destination+" does not exists"));
        StationDistance stationDistance = StationDistance.builder()
                .destination(destinationStation)
                .source(sourceStation)
                .distance(distance).build();
        distanceRepository.save(stationDistance);
        return convertStationDistanceToStationDistanceDTO(stationDistance);
    }

    private StationDistanceDTO convertStationDistanceToStationDistanceDTO(StationDistance stationDistance) {
       return StationDistanceDTO.builder()
                .destinationStationCode(stationDistance.getDestination().getStationCode())
               .sourceStationCode(stationDistance.getSource().getStationCode())
               .distance(stationDistance.getDistance()).build();
    }

}
