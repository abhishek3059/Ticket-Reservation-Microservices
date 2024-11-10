package com.locationSevice.controller;

import com.dto.CommonDTO.LocationDTO;
import com.locationSevice.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/location")
@RequiredArgsConstructor
public class LocationController {


    private final LocationService locationService;


    @GetMapping("/DTO/{locationId}")
    public LocationDTO getLocationDtoByLocationId(@PathVariable Long locationId){
        return locationService.sendLocationDetailsToOtherServices(locationId);
    }
    @GetMapping("/public/view") @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<List<LocationDTO>> getAllStations(){
        return locationService.getAllStations();
    }
    @PostMapping("/private/create") @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<LocationDTO> createStation(@RequestBody LocationDTO station){
        return locationService.createStation(station);
    }
    @GetMapping("/public/{locationId}") @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<LocationDTO> getLocationById(@PathVariable long locationId){
        return locationService.getLocationById(locationId);
    }
    @GetMapping("/public/stationCode/{stationCode}") @ResponseStatus(HttpStatus.ACCEPTED)
    public LocationDTO getLocationByStationCode(@PathVariable String stationCode){
        return locationService.getLocationByStationCode(stationCode);
    }

    @PutMapping("/private/add-trains-from-train-service/{stationName}") @ResponseStatus(HttpStatus.CREATED)
    public void addTrainsFromTrainServiceToGivenStation(@PathVariable  String stationName){
         locationService.addTrainsToStations(stationName);
    }


}
