package com.locationSevice.service;

import com.locationSevice.model.Location;

import java.util.List;

public interface LocationService {

    public List<Location> getAllStations();
    public Location getLocationById(long id);

}
