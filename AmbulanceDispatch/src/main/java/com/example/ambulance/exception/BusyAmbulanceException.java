package com.example.ambulance.exception;

public class BusyAmbulanceException extends RuntimeException {
    public BusyAmbulanceException(String message) {
        super(message);
    }
}
