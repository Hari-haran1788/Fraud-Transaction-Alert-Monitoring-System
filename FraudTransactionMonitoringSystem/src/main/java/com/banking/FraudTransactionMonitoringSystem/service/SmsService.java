package com.banking.FraudTransactionMonitoringSystem.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;

    @Service
    public class SmsService {


        public static final String ACCOUNT_SID = "AC949968221567dbd00ee94cd42f80f26f";
        public static final String AUTH_TOKEN = "22f0cdbb2215e196ed8aea30976fbcc1";
        public static final String TWILIO_NUMBER = "+16626231300";

        public void sendBlockAlert(long userMobile) {
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);


            String toMobile = "+91" + userMobile;

            Message.creator(
                    new PhoneNumber(toMobile),
                    new PhoneNumber(TWILIO_NUMBER),
                    "Strapay Bank Alert: For your protection, your account access has been blocked due to multiple failed authentication attempts. Please visit your nearest branch or contact our 24/7 Security Desk to verify your identity. Ref: STB-882190."
            ).create();

            System.out.println("SMS Sent successfully to: " + toMobile);
        }
    }

