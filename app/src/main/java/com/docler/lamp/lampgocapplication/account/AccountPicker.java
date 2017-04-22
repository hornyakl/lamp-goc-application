package com.docler.lamp.lampgocapplication.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.docler.lamp.lampgocapplication.permission.RequestCodeProvider;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * @see <a href="https://developers.google.com/android/reference/com/google/android/gms/common/AccountPicker">
 * Google Documentation for AccountPicker</a>
 */
public class AccountPicker {
//    private static final Logger LOGGER = Logger.getLogger(AccountPicker.class.getName());

    private final AccountManagerWrapper accountManager;
    private final Activity activity;
    private final Observable<ActivityResult> activityResult;

    public AccountPicker(AccountManagerWrapper accountManager, Activity activity, Observable<ActivityResult> activityResult) {
        this.accountManager = accountManager;
        this.activity = activity;
        this.activityResult = activityResult;
    }

    public <T> ObservableTransformer<T, Account> applyPickAccount(final AccountType accountType) {
        return new ObservableTransformer<T, Account>() {

            @Override
            public ObservableSource<Account> apply(Observable<T> upstream) {
                return upstream
                        .flatMapSingle(
                                new Function<T, SingleSource<Account>>() {
                                    @Override
                                    public SingleSource<Account> apply(T value) throws Exception {
                                        return pickAccount(accountType);
                                    }
                                }
                        )
                        .retry();
            }
        };
    }

    public Single<Account> pickAccount(AccountType accountType) {
        return Single
                .just(accountType)
                .map(
                        new Function<AccountType, Intent>() {
                            @Override
                            public Intent apply(AccountType accountType) throws Exception {
                                return accountManager.newChooseAccountIntent(
                                        null,
                                        null,
                                        new String[]{accountType.getUrl()},
                                        false,
                                        null,
                                        null,
                                        null,
                                        null
                                );
                            }
                        }
                )
                // 1. Return a Single<ActivityResult> for the List<String> permissionsToCheck
                // 2. flatMap will subscribe to this single
                // 3. doOnSubscribe is called, an android acciunt picker intent is fired
                // 4. user sets up account
                // 5. in the activity code, ActivityResult is added to activityResult (which is really a Subject)
                // 6. activityResult emits the item
                // 7. requestCode filter is called to skip results coming for other permission requests
                // 8. firstOrError is called to convert the Observable into a Single
                .flatMap(
                        new Function<Intent, SingleSource<ActivityResult>>() {
                            @Override
                            public SingleSource<ActivityResult> apply(final Intent accountPickerIntent) throws Exception {
                                final int requestCode = RequestCodeProvider.getCode();

                                return activityResult
                                        .doOnSubscribe(
                                                new Consumer<Disposable>() {
                                                    @Override
                                                    public void accept(Disposable d) throws Exception {
                                                        activity.startActivityForResult(accountPickerIntent, requestCode);
                                                    }
                                                }
                                        )
                                        .filter(
                                                new Predicate<ActivityResult>() {
                                                    @Override
                                                    public boolean test(ActivityResult activityResult) throws Exception {
                                                        return activityResult.getRequestCode() == requestCode;
                                                    }
                                                }
                                        )
                                        .firstOrError();
                            }
                        }
                )
                .map(
                        new Function<ActivityResult, Account>() {
                            @Override
                            public Account apply(ActivityResult result) throws Exception {
                                if (result.getResultCode() != Activity.RESULT_OK) {
                                    throw new AccountPickerException("Activity result status is not OK.");
                                }

                                Intent data = result.getData();
                                if (data == null) {
                                    throw new AccountPickerException("Activity result data is null.");
                                }

                                Bundle extras = data.getExtras();
                                if (extras == null) {
                                    throw new AccountPickerException("Activity result data/extra is null.");
                                }

                                String returnedAccountName = extras.getString(AccountManager.KEY_ACCOUNT_NAME);
                                if (returnedAccountName == null) {
                                    throw new AccountPickerException("Activity result account name is null.");
                                }

                                String returnedAccountType = extras.getString(AccountManager.KEY_ACCOUNT_TYPE);
                                if (returnedAccountType == null) {
                                    throw new AccountPickerException("Activity result account type is null.");
                                }

                                try {
                                    Account[] accounts = accountManager.get(activity).getAccountsByType(returnedAccountType);
                                    for (Account account : accounts) {
                                        if (returnedAccountName.equals(account.name)) {
                                            return account;
                                        }
                                    }
                                } catch (SecurityException ex) {
                                    throw new AccountPickerException("Security exception occured.", ex);
                                }

                                throw new AccountPickerException("Account returned by activity result was not found.");
                            }
                        }
                );
    }
}
