package com.banking.FraudTransactionMonitoringSystem.controller;

import com.banking.FraudTransactionMonitoringSystem.model.Customer;
import com.banking.FraudTransactionMonitoringSystem.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer_registration")
@CrossOrigin("*")
public class RegisterController {

    @Autowired
    private RegistrationService registrationService;

    @PostMapping
    public ResponseEntity<String> registerCustomer(@RequestBody Customer customer){
        String message = registrationService.insertCustomerDetail(customer);
        return new ResponseEntity<>(message,HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getCustomers(){
        List<Customer> cusList   = registrationService.getCustomers();
        return new ResponseEntity<>(cusList,HttpStatus.OK);
    }


}
