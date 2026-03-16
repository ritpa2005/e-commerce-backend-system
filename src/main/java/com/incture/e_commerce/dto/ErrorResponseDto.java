package com.incture.e_commerce.dto;

import java.time.LocalDateTime;

public class ErrorResponseDto {

    private int status;
    private String message;
    private String path;
    private LocalDateTime timestamp;

    public ErrorResponseDto(int status, String message, String path) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    public int getStatus() {
        return status;
    }
    public String getMessage() {
        return message;
    }
    public String getPath() {
        return path;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
