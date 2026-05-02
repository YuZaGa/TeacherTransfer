package com.teachertransfer.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmailOtpResponse {

    private String message;
    private String token;

    public EmailOtpResponse() {}

    public EmailOtpResponse(String message) {
        this.message = message;
    }

    public EmailOtpResponse(String message, String token) {
        this.message = message;
        this.token = token;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
