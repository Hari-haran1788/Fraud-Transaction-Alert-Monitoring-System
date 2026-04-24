package com.banking.FraudTransactionMonitoringSystem.serviceImpl;

import com.banking.FraudTransactionMonitoringSystem.dto.*;
import com.banking.FraudTransactionMonitoringSystem.model.Customer;
import com.banking.FraudTransactionMonitoringSystem.model.Transaction;
import com.banking.FraudTransactionMonitoringSystem.repo.RegistrationRepo;
import com.banking.FraudTransactionMonitoringSystem.repo.TransactionRepo;
import com.banking.FraudTransactionMonitoringSystem.service.ActionService;
import com.banking.FraudTransactionMonitoringSystem.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ActionServiceImpl implements ActionService {

    @Autowired
    private RegistrationRepo registrationRepo;

    @Autowired
    private TransactionRepo transactionRepo;

    @Autowired
    private SmsService smsService;

    @Autowired
    private JavaMailSender mailSender;

    private String validateSecurity(Customer user, int enteredPin) {
        if (user.isIs_blocked()) {
            return "❌ ACCOUNT BLOCKED: Unauthorized access detected. Please contact support.";
        }

        if (user.getPin() != enteredPin) {
            int attempts = user.getFailed_attempts() + 1;
            user.setFailed_attempts(attempts);

            if (attempts >= 3) {
                user.setIs_blocked(true);
                registrationRepo.save(user);

                try {
                    smsService.sendBlockAlert(user.getPh_no());
                } catch (Exception e) {
                    System.err.println("SMS Failed to send: " + e.getMessage());
                }

                return "❌ ALERT: 3 failed attempts. Account has been BLOCKED for safety. SMS notification sent.";
            }

            registrationRepo.save(user);
            return "❌ Invalid PIN. Attempts remaining: " + (3 - attempts);
        }

        return "SUCCESS";
    }

    @Override
    @Transactional
    public String recharge(RechargeRequest request) {
        Optional<Customer> userOpt = registrationRepo.findByUser_name(request.getUsername());
        if (userOpt.isEmpty()) return "❌ User not found";

        Customer user = userOpt.get();

        String authStatus = validateSecurity(user, request.getPin());
        if (!authStatus.equals("SUCCESS")) return authStatus;

        if (user.getBalance() < request.getAmount()) return "❌ Insufficient Balance";

        user.setFailed_attempts(0);
        user.setBalance(user.getBalance() - request.getAmount());
        registrationRepo.save(user);

        Transaction txn = new Transaction();
        txn.setUsername(user.getUser_name());
        txn.setType("DEBIT");
        txn.setAmount(request.getAmount());
        txn.setDescription("Mobile Recharge to " + request.getMobile());
        txn.setTimestamp(LocalDateTime.now());

        transactionRepo.save(txn);

        return "✅ Recharge successful";
    }

    @Override
    @Transactional
    public Object processTransaction(TransferRequest request) {
        Optional<Customer> senderOpt = registrationRepo.findByUser_name(request.getUserName());
        if (senderOpt.isEmpty()) return "❌ Sender not found";

        Customer sender = senderOpt.get();

        String authStatus = validateSecurity(sender, request.getPin());
        if (!authStatus.equals("SUCCESS")) {
            return authStatus;
        }

        double amount = request.getAmount();
        if (sender.getBalance() < amount) {
            return "❌ Insufficient Balance";
        }

        sender.setFailed_attempts(0);
        sender.setBalance(sender.getBalance() - amount);
        registrationRepo.save(sender);

        Transaction senderTxn = new Transaction();
        senderTxn.setUsername(sender.getUser_name());
        senderTxn.setType("DEBIT");
        senderTxn.setAmount(amount);
        senderTxn.setDescription("Transfer to " + request.getToUser());
        senderTxn.setTimestamp(LocalDateTime.now());
        transactionRepo.save(senderTxn);

        Optional<Customer> receiverOpt = registrationRepo.findByUser_name(request.getToUser());
        if(receiverOpt.isPresent()) {
            Customer receiver = receiverOpt.get();
            receiver.setBalance(receiver.getBalance() + amount);
            registrationRepo.save(receiver);

            Transaction receiverTxn = new Transaction();
            receiverTxn.setUsername(receiver.getUser_name());
            receiverTxn.setType("CREDIT");
            receiverTxn.setAmount(amount);
            receiverTxn.setDescription("Received from " + sender.getUser_name());
            receiverTxn.setTimestamp(LocalDateTime.now());
            transactionRepo.save(receiverTxn);
        }

        return sender;
    }

    @Override
    public String changePin(ChangePinRequest request) {
        Optional<Customer> userOpt = registrationRepo.findByUser_name(request.getUsername());
        if (userOpt.isEmpty()) return "❌ User not found";
        Customer user = userOpt.get();

        if (user.isIs_blocked()) {
            return "❌ ACCESS DENIED: This account is blocked. PIN change is disabled.";
        }

        if (user.getPin() != request.getOldPin()) return "❌ Old PIN incorrect";
        user.setPin(request.getNewPin());
        registrationRepo.save(user);
        return "✅ PIN changed successfully!!!";
    }

    @Override
    public String updateMobile(UpdateMobileRequest request) {
        Optional<Customer> userOpt = registrationRepo.findByUser_name(request.getUsername());
        if (userOpt.isEmpty()) return "❌ User not found";
        Customer user = userOpt.get();

        if (user.isIs_blocked()) {
            return "❌ ACCESS DENIED: This account is blocked. Mobile update is disabled.";
        }

        if (user.getPh_no() != request.getOldMobile() || user.getPin() != request.getPin())
            return "❌ Old mobile or PIN incorrect";
        user.setPh_no(request.getNewMobile());
        registrationRepo.save(user);
        return "✅ Mobile number updated successfully!!!";
    }


    @Override
    @Transactional
    public String selfReactivate(ReactivateRequest request) {
        Optional<Customer> userOpt = registrationRepo.findByUser_name(request.getUsername());
        if (userOpt.isEmpty()) return "❌ Account not found.";

        Customer user = userOpt.get();


        if (user.getRecoveryEmail() != null &&
                user.getRecoveryEmail().equalsIgnoreCase(request.getRecoveryEmail()) &&
                user.getPin() == request.getPin()) {

            user.setIs_blocked(false);
            user.setFailed_attempts(0);
            registrationRepo.save(user);

            return "✅ Success! Your account is reactivated. You can login now.";
        }

        return "❌ Details do not match our records. Verification failed.";
    }

    @Override
    public String requestReactivationOtp(ReactivateRequest request) {
        Optional<Customer> userOpt = registrationRepo.findByUser_name(request.getUsername());
        if (userOpt.isEmpty()) return "❌ User not found";

        Customer user = userOpt.get();
        if (!user.getRecoveryEmail().equalsIgnoreCase(request.getRecoveryEmail())) {
            return "❌ Recovery details mismatch";
        }


        String generatedOtp = String.valueOf((int)((Math.random() * 900000) + 100000));
        user.setOtp(generatedOtp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5)); // Valid for 5 mins
        registrationRepo.save(user);


        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getRecoveryEmail());
        message.setSubject("Strapay Elite | OTP for Account Reactivation");


        message.setText("Dear " + user.getName() + ",\n\n" +
                "We received a request to reactivate your Strapay Digital Vault.\n\n" +
                "Account Number: " + user.getAccountNumber() + "\n" +
                "Strategic Security Code (OTP): " + generatedOtp + "\n\n" +
                "This code is valid for the next 5 minutes. For your security, Strapay staff will never ask you for this code over the phone or email. If you did not request this reactivation, please secure your account immediately.\n\n" +
                "Stay Secure,\n" +
                "Strapay Elite Security Team");

        mailSender.send(message);

        return "✅ OTP sent to your recovery email.";
    }

    @Override
    @Transactional
    public String verifyOtpAndReactivate(String username, String enteredOtp) {
        Optional<Customer> userOpt = registrationRepo.findByUser_name(username);
        if (userOpt.isEmpty()) return "❌ User not found";

        Customer user = userOpt.get();
        if (user.getOtp() == null || !user.getOtp().equals(enteredOtp)) return "❌ Invalid OTP";
        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) return "❌ OTP Expired";

        user.setIs_blocked(false);
        user.setFailed_attempts(0);
        user.setOtp(null); // Clear OTP after success
        registrationRepo.save(user);

        return "✅ Account Reactivated Successfully!";
    }
}