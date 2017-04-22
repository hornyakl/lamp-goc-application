package com.docler.lamp.lampgocapplication.utils.http;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;

public class HttpRequest {

    final private String url;
    final private List<NameValuePair> parameters;

    public HttpRequest(String url) {
        this(url, new ArrayList<NameValuePair>());
    }

    public HttpRequest(String url, List<NameValuePair> parameters) {
        this.url = url;
        this.parameters = parameters;
    }

    String getUrl() {
        return url;
    }

    List<NameValuePair> getParameters() {
        return parameters;
    }
}
