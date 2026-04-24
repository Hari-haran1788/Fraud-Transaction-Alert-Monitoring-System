package com.banking.FraudTransactionMonitoringSystem.service;

import com.banking.FraudTransactionMonitoringSystem.model.Customer;
import java.util.List;
import java.util.Map;

public interface RegistrationService {

    String insertCustomerDetail(Customer customer);


    Map<String, String> loginCustomer(String username, String password);

    List<Customer> getCustomers();
}