package com.influcollab.exception;

public class InvalidCollaborationRequestException extends RuntimeException {
    public InvalidCollaborationRequestException() {
        super("You cannot send a collaboration request to your own opportunity.");
    }
}
