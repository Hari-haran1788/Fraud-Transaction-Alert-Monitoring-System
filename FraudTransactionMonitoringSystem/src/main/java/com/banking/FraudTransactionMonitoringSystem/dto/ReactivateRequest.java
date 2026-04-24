package com.banking.FraudTransactionMonitoringSystem.dto;

public class ReactivateRequest {
    private String username;
    private String recoveryEmail;


    private Integer pin;


    public ReactivateRequest() {}


    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getRecoveryEmail() {
        return recoveryEmail;
    }
    public void setRecoveryEmail(String recoveryEmail) {
        this.recoveryEmail = recoveryEmail;
    }

    public Integer getPin() {
        return pin;
    }
    public void setPin(Integer pin) {
        this.pin = pin;
    }
}