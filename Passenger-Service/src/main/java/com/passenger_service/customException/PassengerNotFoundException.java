package com.passenger_service.customException;

public class PassengerAlreadyExistsException extends RuntimeException {
    public PassengerAlreadyExistsException(String message) {

        super(message);
    }
}
