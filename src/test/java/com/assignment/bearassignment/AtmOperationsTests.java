package com.assignment.bearassignment;

import com.assignment.bearassignment.exception.MoneyLackException;
import com.assignment.bearassignment.exception.WrongPinException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AtmOperationsTests {

    @Mock
    private BankApiProcessor bankApiProcessor;

    @InjectMocks
    private Atm atm;

    @InjectMocks
    private AccountServiceImpl accountService;

    @DisplayName("카드 삽입 + 핀체크 성공")
    @Test
    void checkPinNumberSuccess() {
        // Data Setting
        String cardNumber = "5272192311221";
        Card card = new Card(cardNumber);
        String accountNUmber = "300411293291204";
        BankApiResponse.AccountInfo accountInfo = new BankApiResponse.AccountInfo();
        accountInfo.setAccountNumber(accountNUmber);
        accountInfo.setName("유선우");
        accountInfo.setCurrentAmount(10000);
        BankApiResponse.CheckPinResponse apiResult = new BankApiResponse.CheckPinResponse();
        apiResult.setMessage(null);
        apiResult.setIsCorrect(true);
        apiResult.setStatus(200);
        apiResult.setAccountInfo(accountInfo);
        Mockito.when(bankApiProcessor.checkPin(card.getCardNumber())).thenReturn(apiResult);

        // TEST ACTION
        atm.insertCard(card);
    }

    @DisplayName("카드 삽입 + 핀체크 실패")
    @Test
    void checkPinNumberFail() {
        // Data Setting
        String cardNumber = "5272192311221";
        Card card = new Card(cardNumber);
        BankApiResponse.CheckPinResponse apiResult = new BankApiResponse.CheckPinResponse();
        apiResult.setMessage(null);
        apiResult.setIsCorrect(false);
        apiResult.setStatus(200);
        apiResult.setAccountInfo(null);
        Mockito.when(bankApiProcessor.checkPin(card.getCardNumber())).thenReturn(apiResult);

        // TEST ACTION
        Assertions.assertThrows(WrongPinException.class, () -> {
            atm.insertCard(card);
        });
    }

    @DisplayName("잔액조회")
    @Test
    void getBalance() {
        // DATA SETTING
        String cardNumber = "5272192311221";
        Card card = new Card(cardNumber);
        String accountNUmber = "300411293291204";
        BankApiResponse.AccountInfo accountInfo = new BankApiResponse.AccountInfo();
        accountInfo.setAccountNumber(accountNUmber);
        accountInfo.setName("유선우");
        accountInfo.setCurrentAmount(10000);
        BankApiResponse.CheckPinResponse apiResult = new BankApiResponse.CheckPinResponse();
        apiResult.setMessage(null);
        apiResult.setIsCorrect(true);
        apiResult.setAccountInfo(accountInfo);
        apiResult.setStatus(200);

        Mockito.when(bankApiProcessor.checkPin(card.getCardNumber())).thenReturn(apiResult);
        Account account = atm.insertCard(card);

        // ACTION TEST
        Assertions.assertEquals(10000, accountService.getBalance(account));
    }

    @DisplayName("출금성공")
    @Test
    void withdrawSuccess() {
        // DATA SETTING
        String cardNumber = "5272192311221";
        Card card = new Card(cardNumber);
        String accountNumber = "300411293291204";
        BankApiResponse.AccountInfo accountInfo = new BankApiResponse.AccountInfo();
        accountInfo.setAccountNumber(accountNumber);
        accountInfo.setName("유선우");
        accountInfo.setCurrentAmount(10000);
        BankApiResponse.CheckPinResponse apiResult = new BankApiResponse.CheckPinResponse();
        apiResult.setMessage(null);
        apiResult.setIsCorrect(true);
        apiResult.setAccountInfo(accountInfo);
        apiResult.setStatus(200);

        Mockito.when(bankApiProcessor.checkPin(card.getCardNumber())).thenReturn(apiResult);
        Account account = atm.insertCard(card);
        Integer withdrawAmount = 3000;
        BankApiResponse.WithdrawResponse withdrawResponse = new BankApiResponse.WithdrawResponse();
        withdrawResponse.setMessage(null);
        withdrawResponse.setCurrentAmount(7000);
        withdrawResponse.setStatus(200);
        Mockito.when(bankApiProcessor.withdraw(account.getAccountNumber(), withdrawAmount)).thenReturn(withdrawResponse);

        // ACTION TEST
        accountService.withdraw(account, withdrawAmount);
        Assertions.assertEquals(10000 - withdrawAmount, accountService.getBalance(account));
    }

    @DisplayName("출금 금액 부족 실패")
    @Test
    void withdrawFail() {
        // DATA SETTING
        String cardNumber = "5272192311221";
        Card card = new Card(cardNumber);
        String accountNumber = "300411293291204";
        BankApiResponse.AccountInfo accountInfo = new BankApiResponse.AccountInfo();
        accountInfo.setAccountNumber(accountNumber);
        accountInfo.setName("유선우");
        accountInfo.setCurrentAmount(10000);
        BankApiResponse.CheckPinResponse apiResult = new BankApiResponse.CheckPinResponse();
        apiResult.setMessage(null);
        apiResult.setIsCorrect(true);
        apiResult.setAccountInfo(accountInfo);
        apiResult.setStatus(200);
        Mockito.when(bankApiProcessor.checkPin(card.getCardNumber())).thenReturn(apiResult);
        Account account = atm.insertCard(card);
        Integer withdrawAmount = 12000;
        BankApiResponse.WithdrawResponse withdrawResponse = new BankApiResponse.WithdrawResponse();
        withdrawResponse.setMessage("lack of money");
        withdrawResponse.setCurrentAmount(10000);
        withdrawResponse.setStatus(200);
        Mockito.when(bankApiProcessor.withdraw(account.getAccountNumber(), withdrawAmount)).thenReturn(withdrawResponse);

        // TEST ACTION
        Assertions.assertThrows(MoneyLackException.class, () -> {
            accountService.withdraw(account, withdrawAmount);
        });
    }

    // 입금
    @Test
    void depositSuccess() {
        // DATA SETTING
        String cardNumber = "5272192311221";
        Card card = new Card(cardNumber);
        String accountNumber = "300411293291204";
        BankApiResponse.AccountInfo accountInfo = new BankApiResponse.AccountInfo();
        accountInfo.setAccountNumber(accountNumber);
        accountInfo.setName("유선우");
        accountInfo.setCurrentAmount(10000);
        BankApiResponse.CheckPinResponse apiResult = new BankApiResponse.CheckPinResponse();
        apiResult.setMessage(null);
        apiResult.setIsCorrect(true);
        apiResult.setAccountInfo(accountInfo);
        apiResult.setStatus(200);
        Mockito.when(bankApiProcessor.checkPin(card.getCardNumber())).thenReturn(apiResult);
        Account account = atm.insertCard(card);
        Integer depositAmount = 12000;
        BankApiResponse.DepositResponse depositResponse = new BankApiResponse.DepositResponse();
        depositResponse.setMessage(null);
        depositResponse.setCurrentAmount(22000);
        depositResponse.setStatus(200);
        Mockito.when(bankApiProcessor.deposit(account.getAccountNumber(), depositAmount)).thenReturn(depositResponse);

        // TEST ACTION
        accountService.deposit(account, depositAmount);
        Assertions.assertEquals(depositResponse.getCurrentAmount(),account.getBalance());
    }
}
