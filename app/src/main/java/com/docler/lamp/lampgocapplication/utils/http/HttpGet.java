package com.docler.lamp.lampgocapplication.utils.http;

import cz.msebera.android.httpclient.NameValuePair;
import io.reactivex.functions.Function;

class HttpGet implements Function<HttpRequest, String> {

    private final HttpCall httpCall;

    public HttpGet(HttpCall httpCall) {
        this.httpCall = httpCall;
    }

    @Override
    public String apply(HttpRequest request) throws Exception {
        String realUrl = request.getUrl() + "?";
        for (NameValuePair parameter : request.getParameters()) {
            realUrl += parameter.getName() + "=" + parameter.getValue() + "&";
        }

        cz.msebera.android.httpclient.client.methods.HttpGet httpGet
                = new cz.msebera.android.httpclient.client.methods.HttpGet(realUrl);

        return httpCall.apply(httpGet);
    }
}