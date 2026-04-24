package com.banking.FraudTransactionMonitoringSystem.controller;

import com.banking.FraudTransactionMonitoringSystem.model.Transaction;
import com.banking.FraudTransactionMonitoringSystem.repo.TransactionRepo;
import com.banking.FraudTransactionMonitoringSystem.dto.*;
import com.banking.FraudTransactionMonitoringSystem.service.ActionService;
import com.banking.FraudTransactionMonitoringSystem.service.PdfGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class ActionController {

    @Autowired
    private ActionService actionService;

    @Autowired
    private TransactionRepo transactionRepo;

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @GetMapping("/transactions/{username}")
    public List<Transaction> getHistory(@PathVariable String username) {
        return transactionRepo.findByUsernameOrderByTimestampDesc(username);
    }

    @GetMapping("/download-statement/{username}")
    public void generatePDF(HttpServletResponse response, @PathVariable String username) throws IOException {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Statement_" + username + ".pdf";
        response.setHeader(headerKey, headerValue);

        List<Transaction> transactions = transactionRepo.findByUsernameOrderByTimestampDesc(username);
        pdfGeneratorService.export(response, username, transactions);
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferRequest request) {
        Object result = actionService.processTransaction(request);
        if (result instanceof String) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/recharge")
    public ResponseEntity<?> recharge(@RequestBody RechargeRequest request) {
        String result = actionService.recharge(request);
        if (result.contains("❌")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/changePin")
    public String changePin(@RequestBody ChangePinRequest request) {
        return actionService.changePin(request);
    }

    @PostMapping("/updateMobile")
    public String updateMobile(@RequestBody UpdateMobileRequest request) {
        return actionService.updateMobile(request);
    }




    @PostMapping("/request-otp")
    public ResponseEntity<String> requestOtp(@RequestBody ReactivateRequest request) {
        String response = actionService.requestReactivationOtp(request);
        if (response.contains("✅")) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestParam String username, @RequestParam String otp) {
        String response = actionService.verifyOtpAndReactivate(username, otp);
        if (response.contains("✅")) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


    @PostMapping("/self-reactivate")
    public ResponseEntity<String> reactivate(@RequestBody ReactivateRequest request) {
        String result = actionService.selfReactivate(request);
        if (result.contains("❌")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }
}