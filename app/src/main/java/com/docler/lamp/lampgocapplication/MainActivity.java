package com.docler.lamp.lampgocapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button = (Button) findViewById(R.id.play_button);
        button.setOnClickListener(new PlayButtonListener());

        final Button createQuestButton = (Button) findViewById(R.id.createQuestButton);
        createQuestButton.setOnClickListener(new CreateQuestButtonListener());

        final Button passedQuestButton = (Button) findViewById(R.id.passedQuestsButton);
        passedQuestButton.setOnClickListener(new PassedQuestsButtonListener());
    }

    private class PlayButtonListener implements View.OnClickListener {
        public void onClick(View v) {
            LampApplication application = (LampApplication) getApplication();
            application.startViewChangeListen(MainActivity.this);
        }
    }

    private class CreateQuestButtonListener implements View.OnClickListener {
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, CreateQuestActivity.class);
            startActivity(intent);
        }
    }

    private class PassedQuestsButtonListener implements View.OnClickListener {
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, PassedQuestsActivity.class);
            startActivity(intent);
        }
    }
}
