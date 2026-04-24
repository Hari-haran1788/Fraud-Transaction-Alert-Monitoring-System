package com.banking.FraudTransactionMonitoringSystem.service;

import com.banking.FraudTransactionMonitoringSystem.dto.LoginResponse;

public interface LoginService {
    LoginResponse login(String username, String password);
}