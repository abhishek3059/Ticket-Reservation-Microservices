package com.locationSevice.repository;

import com.locationSevice.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface LocationRepository extends JpaRepository<Location,Long> {

    Optional<Location> findByStationCode(String stationCode);

    Optional<Location> findByStationName(String stationName);

    Boolean existsByStationName(String stationName);
}
