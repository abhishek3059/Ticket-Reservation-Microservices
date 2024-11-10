package com.locationSevice.service;

import com.dto.CommonDTO.LocationDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface LocationService {

    public ResponseEntity<LocationDTO> createStation(LocationDTO station);
    public ResponseEntity<List<LocationDTO>> getAllStations();
    public ResponseEntity<LocationDTO> getLocationById(long id);
    public LocationDTO getLocationByStationCode(String stationCode);
    public LocationDTO sendLocationDetailsToOtherServices(Long locationId);
    public List<String> fetchTrainsByStationName(String stationName);
    public void addTrainsToStations(String stationName);
}
