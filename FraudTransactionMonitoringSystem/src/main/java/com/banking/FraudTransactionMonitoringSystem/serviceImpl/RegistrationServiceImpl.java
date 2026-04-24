package com.banking.FraudTransactionMonitoringSystem.serviceImpl;

import com.banking.FraudTransactionMonitoringSystem.model.Customer;
import com.banking.FraudTransactionMonitoringSystem.repo.RegistrationRepo;
import com.banking.FraudTransactionMonitoringSystem.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    @Autowired
    RegistrationRepo registrationRepo;


    private String generateUniqueAccountNumber() {
        return "9988" + (long)(Math.random() * 100000000L);
    }

    @Override
    public String insertCustomerDetail(Customer customer) {
        customer.setBalance(50000.0);

        customer.setAccountNumber(generateUniqueAccountNumber());

        Customer saved_cus = registrationRepo.save(customer);
        if (saved_cus != null) {
            return "Successfully inserted customer details";
        }
        return "Customer not inserted";
    }

    @Override
    public List<Customer> getCustomers() {
        return registrationRepo.findAll();
    }


    @Override
    public Map<String, String> loginCustomer(String username, String password) {
        Map<String, String> response = new HashMap<>();
        Optional<Customer> userOpt = registrationRepo.findByUser_name(username);

        if (userOpt.isPresent()) {
            Customer user = userOpt.get();


            if (user.isIs_blocked()) {
                response.put("status", "blocked");
                response.put("message", "❌ ACCESS DENIED: This account is blocked due to multiple failed PIN attempts. Please use the Support section to reactivate.");
                return response;
            }


            if (user.getPassword().equals(password)) {
                response.put("status", "success");
                response.put("message", "Login Successful");
            } else {
                response.put("status", "fail");
                response.put("message", "Invalid Password");
            }
        } else {
            response.put("status", "fail");
            response.put("message", "User not found");
        }
        return response;
    }
}