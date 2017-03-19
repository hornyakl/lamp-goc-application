package com.docler.lamp.lampgocapplication.utils;

import org.jdeferred.Promise;
import org.jdeferred.android.AndroidDeferredManager;
import org.jdeferred.android.DeferredAsyncTask;

import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.CloseableHttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;

public class HttpPromiseClient {
    private final AndroidDeferredManager deferredManager;

    public HttpPromiseClient(AndroidDeferredManager deferredManager) {
        this.deferredManager = deferredManager;
    }

    public Promise<String, Throwable, Void> get(String url) {
        return deferredManager.when(new AsyncHttpGetDeferred(url));
    }

    public Promise<String, Throwable, Void> post(String url, List<NameValuePair> parameters) {
        return deferredManager.when(new AsyncHttpPostDeferred(url, parameters));
    }

    private static class AsyncHttpGetDeferred extends DeferredAsyncTask<Void, Void, String> {
        private String url;

        AsyncHttpGetDeferred(String url) {
            this.url = url;
        }

        @Override
        protected String doInBackgroundSafe(Void... nil) throws Exception {

            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);

            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                return EntityUtils.toString(response.getEntity());
            }
        }
    }

    private static class AsyncHttpPostDeferred extends DeferredAsyncTask<Void, Void, String> {
        private final String url;
        private final List<NameValuePair> parameters;

        AsyncHttpPostDeferred(String url, List<NameValuePair> parameters) {
            this.url = url;
            this.parameters = parameters;
        }

        @Override
        protected String doInBackgroundSafe(Void... nil) throws Exception {

            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(parameters));

            try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
                return EntityUtils.toString(response.getEntity());
            }

        }
    }
}
