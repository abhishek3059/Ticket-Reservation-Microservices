package com.locationSevice.repository;

import com.locationSevice.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location,Long> {

    Optional<Location> findByStationCode(String stationCode);
}
