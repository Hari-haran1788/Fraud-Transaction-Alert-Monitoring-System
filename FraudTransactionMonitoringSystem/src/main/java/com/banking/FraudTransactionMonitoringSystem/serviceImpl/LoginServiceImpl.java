package com.banking.FraudTransactionMonitoringSystem.serviceImpl;

import com.banking.FraudTransactionMonitoringSystem.dto.LoginResponse;
import com.banking.FraudTransactionMonitoringSystem.model.Customer;
import com.banking.FraudTransactionMonitoringSystem.repo.RegistrationRepo;
import com.banking.FraudTransactionMonitoringSystem.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private RegistrationRepo registrationRepo;

    @Override
    public LoginResponse login(String username, String password) {
        Optional<Customer> customerOpt = registrationRepo.findByUser_name(username);

        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            if (customer.getPassword().equals(password)) {
                return new LoginResponse("success", "Login Successful");
            } else {
                return new LoginResponse("fail", "Invalid username or password");
            }
        } else {
            return new LoginResponse("fail", "Invalid username or password");
        }
    }
}