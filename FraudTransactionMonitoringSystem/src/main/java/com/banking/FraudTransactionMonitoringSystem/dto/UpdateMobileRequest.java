package com.banking.FraudTransactionMonitoringSystem.dto;

public class UpdateMobileRequest {
    private String username;
    private long oldMobile;
    private long newMobile;
    private int pin;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public long getOldMobile() { return oldMobile; }
    public void setOldMobile(long oldMobile) { this.oldMobile = oldMobile; }

    public long getNewMobile() { return newMobile; }
    public void setNewMobile(long newMobile) { this.newMobile = newMobile; }

    public int getPin() { return pin; }
    public void setPin(int pin) { this.pin = pin; }
}
