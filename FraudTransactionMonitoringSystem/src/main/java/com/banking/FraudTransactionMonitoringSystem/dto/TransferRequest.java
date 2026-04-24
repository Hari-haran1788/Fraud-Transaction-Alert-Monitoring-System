package com.banking.FraudTransactionMonitoringSystem.dto;

public class TransferRequest {
    private String userName;
    private String toUser;
    private double amount;
    private String type;
    private int pin;


    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getToUser() { return toUser; }
    public void setToUser(String toUser) { this.toUser = toUser; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getPin() { return pin; } // Added Getter
    public void setPin(int pin) { this.pin = pin; } // Added Setter
}