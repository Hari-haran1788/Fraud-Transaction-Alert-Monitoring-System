package com.banking.FraudTransactionMonitoringSystem.controller;

import com.banking.FraudTransactionMonitoringSystem.dto.LoginRequest;
import com.banking.FraudTransactionMonitoringSystem.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class LoginController {

    @Autowired


    private RegistrationService registrationService;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request) {

        System.out.println("Login attempt for user: " + request.getUsername());

        return registrationService.loginCustomer(request.getUsername(), request.getPassword());
    }
}