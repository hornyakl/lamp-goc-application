package com.docler.lamp.lampgocapplication;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class AcceptQuest extends AppCompatActivity {
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_quest);

        getSupportActionBar().hide();

        LampApplication application = (LampApplication) getApplication();

        WebView acceptQuestWebView = (WebView) findViewById(R.id.acceptQuestView);

        WebSettings webSettings = acceptQuestWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        acceptQuestWebView.setWebChromeClient(new WebChromeClient());

        acceptQuestWebView.loadUrl("https://goc-lamp.tk/quest?quest_id=" + application.currentQuest.getId());
    }
}
