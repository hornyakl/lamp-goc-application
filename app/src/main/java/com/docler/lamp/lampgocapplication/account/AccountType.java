package com.docler.lamp.lampgocapplication.account;

public enum AccountType {

    GOOGLE("com.google"),
    FACEBOOK("com.facebook.auth.login");

    private String url;

    AccountType(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
