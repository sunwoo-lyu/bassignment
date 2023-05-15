package com.assignment.bearassignment;

import lombok.Getter;
import lombok.Setter;

public class BankApiResponse {

    @Getter
    @Setter
    public static class WithdrawResponse {
        private Integer status;
        private Integer currentAmount;
        private String message;
    }

    @Getter
    @Setter
    public static class DepositResponse {
        private Integer status;
        private Integer currentAmount;
        private String message;
    }

    @Getter
    @Setter
    public static class SeeBalanceResponse {
        private Integer status;
        private Integer currentAmount;
        private String message;
    }

    @Getter
    @Setter
    public static class CheckPinResponse {
        private Integer status;
        private Boolean isCorrect;
        private String message;
        private AccountInfo accountInfo;

    }

    @Getter
    @Setter
    public static class AccountInfo {
        private String accountNumber;
        private Integer currentAmount;
        private String name;
    }

}
