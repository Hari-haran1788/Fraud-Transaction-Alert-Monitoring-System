package com.banking.FraudTransactionMonitoringSystem.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Customer_Register_Table")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer customer_id;
    private String name;
    private long ph_no;
    private String user_name;
    private String password;
    private int pin;
    private Double balance;
    private String recoveryEmail;
    private String otp;
    private LocalDateTime otpExpiry;


    private String accountNumber;


    private int failed_attempts = 0;
    private boolean is_blocked = false;

    public Customer() {}

    public Customer(Integer customer_id, String name, long ph_no, String user_name, String password, int pin, Double balance, String recoveryEmail, String accountNumber) {
        this.customer_id = customer_id;
        this.name = name;
        this.ph_no = ph_no;
        this.user_name = user_name;
        this.password = password;
        this.pin = pin;
        this.balance = balance;
        this.recoveryEmail = recoveryEmail;
        this.accountNumber = accountNumber;
        this.otp = "";
        this.otpExpiry = LocalDateTime.now();
    }


    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }


    public int getFailed_attempts() { return failed_attempts; }
    public void setFailed_attempts(int failed_attempts) { this.failed_attempts = failed_attempts; }

    public boolean isIs_blocked() { return is_blocked; }
    public void setIs_blocked(boolean is_blocked) { this.is_blocked = is_blocked; }


    public Integer getCustomer_id() { return customer_id; }
    public void setCustomer_id(Integer customer_id) { this.customer_id = customer_id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public long getPh_no() { return ph_no; }
    public void setPh_no(long ph_no) { this.ph_no = ph_no; }
    public String getUser_name() { return user_name; }
    public void setUser_name(String user_name) { this.user_name = user_name; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public int getPin() { return pin; }
    public void setPin(int pin) { this.pin = pin; }
    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }
    public String getRecoveryEmail() { return recoveryEmail; }
    public void setRecoveryEmail(String recoveryEmail) { this.recoveryEmail = recoveryEmail; }
    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
    public LocalDateTime getOtpExpiry() { return otpExpiry; }
    public void setOtpExpiry(LocalDateTime otpExpiry) { this.otpExpiry = otpExpiry; }
}