package com.train_service.customException;

public class TrainNotFound extends RuntimeException {
    public TrainNotFound(String message) {

        super(message);
    }
}
