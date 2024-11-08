package com.locationSevice.serviceImpl;

import com.locationSevice.customException.StationNotFound;
import com.locationSevice.model.Location;
import com.locationSevice.repository.LocationRepository;
import com.locationSevice.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class LocationServiceImpl implements LocationService {

    @Autowired
    private LocationRepository locationRepository;
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

}
