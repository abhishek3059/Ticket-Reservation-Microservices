package com.locationSevice.serviceImpl;

import com.dto.CommonDTO.LocationDTO;
import com.dto.CommonDTO.TrainDTO;
import com.locationSevice.customException.StationNotFound;
import com.locationSevice.model.Location;
import com.locationSevice.repository.LocationRepository;
import com.locationSevice.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

public class LocationServiceImpl implements LocationService {

    private final WebClient client;
    private final LocationRepository locationRepository;

    public LocationServiceImpl(WebClient.Builder client, LocationRepository locationRepository) {
        this.client = client.baseUrl("http://TRAIN-SERVICE").build();
        this.locationRepository = locationRepository;
    }

    @Override
    public List<Location> getAllStations() {
        return locationRepository.findAll();
    }

    @Override
    public Location getLocationById(long id) {
        return locationRepository.findById(id).
                    orElseThrow(() ->
                            new StationNotFound("Station by id:"+id+" not found"));
    }

    @Override
    public Optional<Location> getLocationByStationCode(String stationCode) {
        return locationRepository.findByStationCode(stationCode);
    }

    @Override
    public LocationDTO sendLocationDetailsToOtherServices(Long locationId) {
          Location location = locationRepository.findById(locationId)
                  .orElseThrow(() -> new StationNotFound("Station requested is not found"));
        if(location != null){
            LocationDTO dto = LocationDTO.builder()
                    .locationId(location.getLocationId())
                    .stationCode(location.getStationCode())
                    .stationName(location.getStationName())
                    .state(location.getState())
                    .city(location.getCity()).build();
            return dto;
        }
        else throw new StationNotFound("Station requested not found");
    }

    @Override
    public List<TrainDTO> fetchTrainsByStationName(String stationName) {
        return List.of();
    }


}
