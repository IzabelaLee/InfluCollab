package com.influcollab.exception;

public class UnauthorizedRequestException extends RuntimeException {
    public UnauthorizedRequestException() {
        super("Unauthorized request");
    }
}
