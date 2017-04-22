package com.docler.lamp.lampgocapplication.utils.http;

import io.reactivex.functions.Function;

public class HttpFunctionsFactory {
    public Function<HttpRequest, String> createGet() {
        return new HttpGet(
                new HttpCall()
        );
    }

    public Function<HttpRequest, String> createPost() {
        return new HttpPost(
                new HttpCall()
        );
    }
}
