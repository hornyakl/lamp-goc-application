package com.docler.lamp.lampgocapplication.utils.http;

import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import io.reactivex.functions.Function;

class HttpPost implements Function<HttpRequest, String> {
    private final HttpCall httpCall;

    public HttpPost(HttpCall httpCall) {
        this.httpCall = httpCall;
    }

    @Override
    public String apply(HttpRequest request) throws Exception {
        cz.msebera.android.httpclient.client.methods.HttpPost httpPost
                = new cz.msebera.android.httpclient.client.methods.HttpPost(request.getUrl());
        httpPost.setEntity(new UrlEncodedFormEntity(request.getParameters()));

        return httpCall.apply(httpPost);
    }
}
