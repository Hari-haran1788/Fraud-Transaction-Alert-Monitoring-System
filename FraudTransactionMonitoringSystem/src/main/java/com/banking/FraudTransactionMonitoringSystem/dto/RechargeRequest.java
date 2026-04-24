package com.banking.FraudTransactionMonitoringSystem.dto;

public class RechargeRequest {
    private String username;
    private String mobile;
    private double amount;
    private int pin;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public int getPin() { return pin; }
    public void setPin(int pin) { this.pin = pin; }
}