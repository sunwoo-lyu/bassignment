package com.assignment.bearassignment;

import com.assignment.bearassignment.exception.WrongPinException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class Atm {
    private final BankApiProcessor bankApiProcessor;

    public Account insertCard(Card card) {
        BankApiResponse.CheckPinResponse checkPinResponse = bankApiProcessor.checkPin(card.getCardNumber());
        if(checkPinResponse.getIsCorrect()) {
            BankApiResponse.AccountInfo accountInfo = checkPinResponse.getAccountInfo();
            return Account.of(accountInfo.getCurrentAmount(), accountInfo.getAccountNumber(), accountInfo.getName());
        } else {
            throw new WrongPinException("PIN number you submitted is wrong");
        }
    }

}
