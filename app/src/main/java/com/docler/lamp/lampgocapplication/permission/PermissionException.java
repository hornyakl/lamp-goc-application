package com.docler.lamp.lampgocapplication.permission;

public class PermissionException extends RuntimeException {
    public PermissionException() {
        super("Some permissions were not granted.");
    }
}
