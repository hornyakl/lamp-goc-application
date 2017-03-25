package com.docler.lamp.lampgocapplication.utils;

import org.jdeferred.android.AndroidDeferredManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.CloseableHttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Function;

public class HttpSingles {

    public Single<String> get(String url) {
        return get(url, new ArrayList<>());
    }

    public Single<String> get(String url, List<NameValuePair> parameters) {
        return Single
                .create(
                        (SingleEmitter<HttpUriRequest> emitter) -> {
                            String realUrl = url + "?";
                            for (NameValuePair parameter : parameters) {
                                realUrl += parameter.getName() + "=" + parameter.getValue() + "&";
                            }

                            emitter.onSuccess(new HttpGet(realUrl));
                        }
                )
                .map(new HttpCall())
                ;
    }

    public Single<String> post(String url, List<NameValuePair> parameters) {
        return Single
                .create(
                        (SingleEmitter<HttpUriRequest> emitter) -> {
                            HttpPost httpPost = new HttpPost(url);
                            httpPost.setEntity(new UrlEncodedFormEntity(parameters));
                            emitter.onSuccess(httpPost);
                        }
                )
                .map(new HttpCall())
                ;
    }

    private static class HttpCall implements Function<HttpUriRequest, String> {

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
}
