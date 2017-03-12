package com.docler.lamp.lampgocapplication;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class AcceptQuest extends AppCompatActivity {
    private LampApplication application;

    /**
     * WebChromeClient subclass handles UI-related calls.
     */
    private class AcceptQuestWebChromeClient extends WebChromeClient {
        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            Log.d(
                "LampGoc - AcceptQuest ", consoleMessage.message() + " -- From line "
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
        setContentView(R.layout.activity_accept_quest);

        getSupportActionBar().hide();

        application = (LampApplication) getApplication();

        WebView acceptQuestWebView = (WebView) findViewById(R.id.acceptQuestView);

        WebSettings webSettings = acceptQuestWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setGeolocationEnabled(true);

        acceptQuestWebView.setWebChromeClient(new AcceptQuest.AcceptQuestWebChromeClient());

        acceptQuestWebView.loadUrl("https://goc-lamp.tk/quest?quest_id=" + application.currentQuest.getId());
    }
}
