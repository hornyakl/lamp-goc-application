package com.docler.lamp.lampgocapplication;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class CreateQuestActivity extends AppCompatActivity
{
    /**
     * WebChromeClient subclass handles UI-related calls.
     */
    private class GeoWebChromeClient extends WebChromeClient {
        @Override
        public void onGeolocationPermissionsShowPrompt(
                String origin,
                GeolocationPermissions.Callback callback
        ) {
            // Always grant permission since the app itself
            // requires location permission and the user
            // has therefore already granted it.
            callback.invoke(origin, true, false);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quest);

        WebView createQuestWebView = (WebView) findViewById(R.id.createQuestView);

        WebSettings webSettings = createQuestWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setGeolocationEnabled(true);

        createQuestWebView.setWebChromeClient(new GeoWebChromeClient());

        createQuestWebView.loadUrl("https://goc-lamp.tk");
    }
}
