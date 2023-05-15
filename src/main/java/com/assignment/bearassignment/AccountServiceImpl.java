package com.assignment.bearassignment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {
    private final BankApiProcessor bankApiProcessor;

    @Override
    public int getBalance(Account account) {
        return account.getBalance();
    }

    @Override
    public void deposit(Account account, Integer amount) {
        bankApiProcessor.deposit(account.getAccountNumber(), amount);
        account.deposit(amount);
    }

    @Override
    public void withdraw(Account account, Integer amount) {
        bankApiProcessor.withdraw(account.getAccountNumber(), amount);
        account.withdraw(amount);
    }

}
