package com.example.web_ai.exception;

public class FaceRecognitionApiException extends RuntimeException {
    public FaceRecognitionApiException(String message) {
        super(message);
    }

    public FaceRecognitionApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
