package com.parkingoffice.exception;

public class ParkingException extends Exception {
    public ParkingException(String message) {
        super(message);
    }
    
    public ParkingException(String message, Throwable cause) {
        super(message, cause);
    }
}
