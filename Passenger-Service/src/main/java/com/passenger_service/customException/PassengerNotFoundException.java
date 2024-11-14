package com.passenger_service.customException;

public class PassengerNotFoundException extends RuntimeException {
    public PassengerNotFoundException(String message) {

        super(message);
    }
}
