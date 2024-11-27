package com.booking_service.customException;

public class SeatNotAvailable extends RuntimeException {
    public SeatNotAvailable(String message) {
        super(message);
    }
}
