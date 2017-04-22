package com.docler.lamp.lampgocapplication.account;

public class AccountPickerException extends RuntimeException {
    public AccountPickerException(String message) {
        super(message);
    }

    public AccountPickerException(String message, Throwable cause) {
        super(message, cause);
    }
}
