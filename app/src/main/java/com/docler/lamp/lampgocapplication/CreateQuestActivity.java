package com.docler.lamp.lampgocapplication;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class CreateQuestActivity extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quest);

        WebView createQuestWebView = (WebView) findViewById(R.id.createQuestView);

        WebSettings webSettings = createQuestWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);


        createQuestWebView.loadUrl("http://www.agocs.hu");
    }
}
