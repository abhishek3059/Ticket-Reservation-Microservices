package com.train_service.repository;

import com.train_service.enums.ClassType;
import com.train_service.model.SeatAllocation;
import com.train_service.model.Train;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeatAllocationRepository extends JpaRepository<SeatAllocation,Long> {
    List<SeatAllocation> findAllByClassType(ClassType classType);
    Optional<SeatAllocation> findByBookingId(String bookingId);

    List<SeatAllocation> findAllByClassTypeAndTrain(ClassType classType, Train train);
}
