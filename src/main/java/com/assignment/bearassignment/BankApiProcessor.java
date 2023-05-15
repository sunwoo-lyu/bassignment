package com.assignment.bearassignment;


/***
 This class is responsible for communicating with the bank API.
 해당 클래스는 은행의 API와 통신하는 책임을 수행합니다.
 */

public interface BankApiProcessor {
    BankApiResponse.CheckPinResponse checkPin(String cardNumber);
    BankApiResponse.WithdrawResponse withdraw(String accountNumber, Integer amount);
    BankApiResponse.DepositResponse deposit(String accountNumber, Integer amount);
}
