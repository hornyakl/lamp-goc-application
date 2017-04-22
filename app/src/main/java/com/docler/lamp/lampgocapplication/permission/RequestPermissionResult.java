package com.docler.lamp.lampgocapplication.permission;

import android.support.annotation.NonNull;

public class RequestPermissionResult {
    private final int requestCode;

    @NonNull
    private final String[] permissions;

    @NonNull
    private final int[] grantResults;

    public RequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        this.requestCode = requestCode;
        this.permissions = permissions;
        this.grantResults = grantResults;
    }

    public int getRequestCode() {
        return requestCode;
    }

    @NonNull
    public String[] getPermissions() {
        return permissions;
    }

    @NonNull
    public int[] getGrantResults() {
        return grantResults;
    }
}
