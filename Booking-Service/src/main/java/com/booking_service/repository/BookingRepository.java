package com.booking_service.repository;

import com.booking_service.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking,String> {
    Optional<Booking> findBySeatNumber(Integer seatNumber);
}
