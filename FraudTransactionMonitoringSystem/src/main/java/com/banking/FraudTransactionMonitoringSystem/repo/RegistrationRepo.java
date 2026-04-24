package com.banking.FraudTransactionMonitoringSystem.repo;

import com.banking.FraudTransactionMonitoringSystem.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RegistrationRepo extends JpaRepository<Customer, Integer> {

    @Query("SELECT c FROM Customer c WHERE c.user_name = :user_name")
    Optional<Customer> findByUser_name(String user_name);
}