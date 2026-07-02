package com.influcollab.exception;

public class CollabNotFoundException extends RuntimeException {
    public CollabNotFoundException(Long id) {
        super("Collaboration opportunity with id " + id + " not found");
    }
}
