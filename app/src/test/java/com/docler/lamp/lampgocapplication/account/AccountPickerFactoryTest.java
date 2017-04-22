package com.docler.lamp.lampgocapplication.account;

import android.accounts.Account;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;

import io.reactivex.functions.Consumer;

@RunWith(MockitoJUnitRunner.class)
public class AccountPickerFactoryTest {

    @Captor
    private ArgumentCaptor<Consumer<ActivityResult>> captor;

    @Test
    public void testCreateAccountPicker() {
//        try {
//            AccountManagerWrapper mockAccountManagerWrapper = mock(AccountManagerWrapper.class);
//
//            Intent accountPickerIntent = new Intent();
//
//            when(mockAccountManagerWrapper.newChooseAccountIntent(
//                    any(),
//                    any(),
//                    eq(new String[]{AccountType.FACEBOOK.getUrl()}),
//                    anyBoolean(),
//                    any(),
//                    any(),
//                    any(),
//                    any()
//            )).thenReturn(accountPickerIntent);
//
//            MockActivity mockActivity = mock(MockActivity.class);
//
//            AccountPicker accountPicker = new AccountPicker(mockAccountManagerWrapper, activity);
//            Single<Account> single = accountPicker.createAccountPicker(mockActivity, AccountType.FACEBOOK);
//            single.subscribe(
//                    (Account account) -> {
//                        Assert.assertTrue(true);
//                    }
//            );
//
//            verify(mockActivity).subscribeToRegisterActivityResult(captor.capture());
//            verify(mockActivity).startActivityForResult(accountPickerIntent, 100);
//
//            Bundle bundle = mock(Bundle.class);
//            when(bundle.getString(AccountManager.KEY_ACCOUNT_NAME)).thenReturn("testaccount");
//            when(bundle.getString(AccountManager.KEY_ACCOUNT_TYPE)).thenReturn("facebook");
//
//            Intent resultIntent = mock(Intent.class);
//            when(resultIntent.getExtras()).thenReturn(bundle);
//
//            AccountManager mockAccountManager = mock(AccountManager.class);
//            Account account = getMockedAccount("testaccount", "facebook");
//
//            when(mockAccountManager.getAccountsByType(any())).thenReturn(new Account[]{account});
//            when(mockAccountManagerWrapper.get(any())).thenReturn(mockAccountManager);
//
//            captor.getValue().accept(new ActivityResult(100, Activity.RESULT_OK, resultIntent));
//
//        } catch (Throwable t) {
//            t.printStackTrace();
//        }
    }

    private Account getMockedAccount(String accountName, String accountType) {
        Account account = new Account(accountName, accountType);
        try {
            Field nameField = Account.class.getDeclaredField("name");
            nameField.setAccessible(true);
            nameField.set(account, accountName);

            Field typeField = Account.class.getDeclaredField("type");
            typeField.setAccessible(true);
            typeField.set(account, accountType);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return account;
    }
}
