package com.project.airbnb_app.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String s) {
        super(s);
    }
}
