package com.locationSevice.service;

import com.dto.CommonDTO.LocationDTO;
import com.dto.CommonDTO.TrainDTO;
import com.locationSevice.model.Location;

import java.util.List;
import java.util.Optional;

public interface LocationService {

    public List<Location> getAllStations();
    public Location getLocationById(long id);
    public Optional<Location> getLocationByStationCode(String stationCode);
    public LocationDTO sendLocationDetailsToOtherServices(Long locationId);
    public List<TrainDTO> fetchTrainsByStationName(String stationName);
}
