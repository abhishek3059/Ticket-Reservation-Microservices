package com.locationSevice.repository;

import com.locationSevice.model.StationDistance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DistanceRepository extends JpaRepository<StationDistance,Long> {
    Optional<StationDistance> findBySourceStationCodeAndDistanceStationCode
            (String sourceStationCode,String distanceStationCode);
}
