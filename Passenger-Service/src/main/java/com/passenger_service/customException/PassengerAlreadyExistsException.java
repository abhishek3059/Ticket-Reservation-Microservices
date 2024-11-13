package com.train_service.customException;

public class TrainAlreadyExists extends RuntimeException {
    public TrainAlreadyExists(String message) {

        super(message);
    }
}
