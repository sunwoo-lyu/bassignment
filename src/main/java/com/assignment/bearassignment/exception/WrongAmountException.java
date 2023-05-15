package com.assignment.bearassignment.exception;

import java.security.InvalidParameterException;

public class WrongAmountException extends InvalidParameterException {

    public WrongAmountException(String message) {
        super(message);
    }

}
