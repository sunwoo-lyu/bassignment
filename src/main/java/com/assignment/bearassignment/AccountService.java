package com.assignment.bearassignment;

public interface AccountService {
    int getBalance(Account account);

    void deposit(Account account, Integer amount);

    void withdraw(Account account, Integer amount);
}
