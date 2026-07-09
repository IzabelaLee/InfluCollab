package com.influcollab.exception;

public class CollaborationRequestNotFound extends RuntimeException {
    public CollaborationRequestNotFound() {
        super("Collaboration request not found");
    }
}
