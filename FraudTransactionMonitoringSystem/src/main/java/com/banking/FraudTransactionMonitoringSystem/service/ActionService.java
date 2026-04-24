package com.banking.FraudTransactionMonitoringSystem.service;

import com.banking.FraudTransactionMonitoringSystem.dto.*;

public interface ActionService {

    Object processTransaction(TransferRequest request);
    String recharge(RechargeRequest request);
    String changePin(ChangePinRequest request);
    String updateMobile(UpdateMobileRequest request);



    String requestReactivationOtp(ReactivateRequest request);


    String verifyOtpAndReactivate(String username, String enteredOtp);

    String selfReactivate(ReactivateRequest request);
}