package com.parkingoffice.exception;

public class VehicleAlreadyParkedException extends ParkingException {
    public VehicleAlreadyParkedException(String message) {
        super(message);
    }
}
