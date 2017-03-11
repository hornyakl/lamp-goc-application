package com.docler.lamp.lampgocapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class PassedQuestsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passed_quests);

        WebView passedQuestWebView = (WebView) findViewById(R.id.passedQuestsView);

        passedQuestWebView.setWebChromeClient(new WebChromeClient());

        passedQuestWebView.loadUrl("https://goc-lamp.tk/achievements");
    }
}
