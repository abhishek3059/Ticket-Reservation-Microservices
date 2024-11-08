package com.locationSevice.customException;

public class StationNotFound extends RuntimeException {
    public StationNotFound(String message) {
        super(message);
    }
}
