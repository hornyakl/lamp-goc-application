package com.docler.lamp.lampgocapplication.permission;

public class RequestCodeProvider {

    private static int nextCode = 100;

    public static int getCode() {
        return nextCode++;
    }
}
