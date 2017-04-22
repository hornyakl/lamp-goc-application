package com.docler.lamp.lampgocapplication.utils.http;

import java.io.IOException;

import cz.msebera.android.httpclient.client.methods.CloseableHttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Function;

class HttpCall implements Function<HttpUriRequest, String> {

    @Override
    public String apply(HttpUriRequest httpUriRequest) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try (CloseableHttpResponse response = httpclient.execute(httpUriRequest)) {
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }
}