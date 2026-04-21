package com.finance.backend.dto;

public class InsightDto {

    private String message;

    public InsightDto() {
    }

    public InsightDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}