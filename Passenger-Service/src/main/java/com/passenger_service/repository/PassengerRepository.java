package com.passenger_service.repository;

import com.passenger_service.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerRepository extends JpaRepository<Passenger,String> {
}
