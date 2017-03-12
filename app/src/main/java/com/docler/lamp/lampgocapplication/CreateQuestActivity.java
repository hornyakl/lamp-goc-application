package com.docler.lamp.lampgocapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ConsoleMessage;
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
            // Add permission for gps and let user grant the permission
            if (ActivityCompat.checkSelfPermission(
                CreateQuestActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    CreateQuestActivity.this,
                    new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    666
                );

                return;
            }

            callback.invoke(origin, true, true);
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            Log.d(
                "LampGoc", consoleMessage.message() + " -- From line "
                + consoleMessage.lineNumber() + " of "
                + consoleMessage.sourceId()
            );

            return super.onConsoleMessage(consoleMessage);
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

        createQuestWebView.loadUrl("https://goc-lamp.tk/quest-create");
    }
}
