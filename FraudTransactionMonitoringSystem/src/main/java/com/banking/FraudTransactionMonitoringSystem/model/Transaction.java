package com.banking.FraudTransactionMonitoringSystem.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Transaction_History_Table")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String type;
    private double amount;
    private String description;
    private LocalDateTime timestamp;


    public Transaction() {}


    public Transaction(String username, String type, double amount, String description, LocalDateTime timestamp) {
        this.username = username;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.timestamp = timestamp;
    }


    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public LocalDateTime getTimestamp() { return timestamp; }

    public void setUsername(String username) { this.username = username; }
    public void setType(String type) { this.type = type; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setDescription(String description) { this.description = description; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}