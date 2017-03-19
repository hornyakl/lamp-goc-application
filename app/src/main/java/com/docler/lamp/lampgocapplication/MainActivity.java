package com.docler.lamp.lampgocapplication;

import android.accounts.AccountManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.AccountPicker;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Map<String, String> supportedAccountConnectionTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initSupportedAccountConnectionTypes();

        final Button button = (Button) findViewById(R.id.play_button);
        button.setOnClickListener(new PlayButtonListener());

        final Button createQuestButton = (Button) findViewById(R.id.createQuestButton);
        createQuestButton.setOnClickListener(new CreateQuestButtonListener());

        final Button passedQuestButton = (Button) findViewById(R.id.passedQuestsButton);
        passedQuestButton.setOnClickListener(new PassedQuestsButtonListener());

        final Button googleConnectButton = (Button) findViewById(R.id.googleConnectButton);
        googleConnectButton.setOnClickListener(new ConnectButtonListener(googleConnectButton));

        final Button facebookConnectButton = (Button) findViewById(R.id.facebookConnectButton);
        facebookConnectButton.setOnClickListener(new ConnectButtonListener(facebookConnectButton));

        getSupportActionBar().hide();
    }

    private void initSupportedAccountConnectionTypes() {
        supportedAccountConnectionTypes = new HashMap<>();

        supportedAccountConnectionTypes.put("googleConnect", "com.google");
        supportedAccountConnectionTypes.put("facebookConnect", "com.facebook.auth.login");
    }

    /**
     * The account picker activity will return when the user has selected and / or created an account,
     * and the resulting account name can be retrieved here.
     *
     * @param requestCode int
     * @param resultCode  int
     * @param data        Intent
     */
    @Override
    protected void onActivityResult(
        final int requestCode,
        final int resultCode,
        final Intent data
    ) {
        if (
            requestCode == 100
                && resultCode == RESULT_OK
            ) {
            // @todo . use this accountName (email address) as the user's identification.
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        }
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

    private class ConnectButtonListener implements View.OnClickListener {
        Button connectButton;

        ConnectButtonListener(Button clickedConnectButton) {
            connectButton = clickedConnectButton;
        }

        @Override
        public void onClick(View v)
        {
            // @see: https://developers.google.com/android/reference/com/google/android/gms/common/AccountPicker
            Intent accountPickerIntent = AccountPicker.newChooseAccountIntent(
                null,
                null,
                new String[]{supportedAccountConnectionTypes.get(connectButton.getTag().toString())},
                false,
                null,
                null,
                null,
                null
            );

            startActivityForResult(accountPickerIntent, 100);
        }
    }
}
