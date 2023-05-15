package com.assignment.bearassignment;

import com.assignment.bearassignment.exception.MoneyLackException;
import com.assignment.bearassignment.exception.WrongAmountException;

public class Account {

    private Integer currentAmount;
    private String accountNumber;

    public static Account of(Integer currentAmount, String accountNumber, String name) {
        Account instance = new Account();
        instance.currentAmount = currentAmount;
        instance.accountNumber = accountNumber;
        return instance;
    }

    public Integer getBalance() {
        return this.currentAmount;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public void withdraw(Integer amount) {
        if(amount <= 0) {
            throw new WrongAmountException("you can withdraw amount bigger than 0.");
        }
        else if(amount > currentAmount) {
            throw new MoneyLackException("you don't have enough money in your account.");
        }
        this.currentAmount -= amount;
    }

    public void deposit(Integer amount) {
        if(amount <= 0) {
            throw new WrongAmountException("you can deposit amount bigger than 0.");
        }
        this.currentAmount += amount;

    }
}
