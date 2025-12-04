package com.example.web_ai.exception;

public class FaceVerificationFailedException extends RuntimeException {
    public FaceVerificationFailedException(String message) {
        super(message);
    }
}
