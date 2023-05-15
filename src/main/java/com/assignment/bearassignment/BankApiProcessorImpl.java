package com.assignment.bearassignment;

import org.springframework.stereotype.Component;

@Component
public class BankApiProcessorImpl implements BankApiProcessor {

    @Override
    public BankApiResponse.CheckPinResponse checkPin(String cardNumber) {
        return null;
    }

    @Override
    public BankApiResponse.WithdrawResponse withdraw(String accountNumber, Integer amount) {
        return null;
    }

    @Override
    public BankApiResponse.DepositResponse deposit(String accountNumber, Integer amount) {
        return null;
    }
}
