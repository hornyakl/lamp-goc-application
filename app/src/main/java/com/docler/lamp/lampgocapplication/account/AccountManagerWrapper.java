package com.docler.lamp.lampgocapplication.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

public class AccountManagerWrapper {

    public AccountManager get(Context context) {
        return AccountManager.get(context);
    }

    public Intent newChooseAccountIntent(
            Account selectedAccount,
            ArrayList<Account> allowableAccounts,
            String[] allowableAccountTypes,
            boolean alwaysPromptForAccount,
            String descriptionOverrideText,
            String addAccountAuthTokenType,
            String[] addAccountRequiredFeatures,
            Bundle addAccountOptions) {
        return AccountManager.newChooseAccountIntent(
                selectedAccount,
                allowableAccounts,
                allowableAccountTypes,
                alwaysPromptForAccount,
                descriptionOverrideText,
                addAccountAuthTokenType,
                addAccountRequiredFeatures,
                addAccountOptions
        );
    }
}
