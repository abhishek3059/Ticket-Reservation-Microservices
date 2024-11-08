package com.train_service.repository;

import com.train_service.model.Train;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainRepository extends JpaRepository<Train,Long> {
    public Optional<Train> findByTrainNumber(String trainNumber);
}
