package com.locationSevice.controller;

import com.dto.CommonDTO.LocationDTO;
import com.locationSevice.customException.StationNotFound;
import com.locationSevice.model.Location;
import com.locationSevice.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("api/location")
@RequiredArgsConstructor
public class LocationController {


    private final LocationService locationService;


    @GetMapping("/DTO/{locationId}")
    public LocationDTO fetchLocationDtoByLocationId(@PathVariable Long locationId){
        return locationService.sendLocationDetailsToOtherServices(locationId);

    }


}
