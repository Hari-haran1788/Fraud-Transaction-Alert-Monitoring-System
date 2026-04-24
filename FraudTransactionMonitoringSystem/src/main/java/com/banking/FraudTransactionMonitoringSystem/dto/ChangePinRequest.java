package com.banking.FraudTransactionMonitoringSystem.dto;

public class ChangePinRequest {
    private String username;
    private int oldPin;
    private int newPin;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getOldPin() { return oldPin; }
    public void setOldPin(int oldPin) { this.oldPin = oldPin; }

    public int getNewPin() { return newPin; }
    public void setNewPin(int newPin) { this.newPin = newPin; }
}
