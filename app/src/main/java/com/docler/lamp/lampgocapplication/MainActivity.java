package com.docler.lamp.lampgocapplication;

import android.accounts.Account;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.docler.lamp.lampgocapplication.account.AccountManagerWrapper;
import com.docler.lamp.lampgocapplication.account.AccountPicker;
import com.docler.lamp.lampgocapplication.account.AccountType;
import com.docler.lamp.lampgocapplication.account.ActivityResult;
import com.docler.lamp.lampgocapplication.permission.PermissionChecker;
import com.docler.lamp.lampgocapplication.permission.RequestPermissionResult;
import com.docler.lamp.lampgocapplication.view.DisablingListener;

import java.util.logging.Level;
import java.util.logging.Logger;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class MainActivity extends AppCompatActivity {
    private static final Logger LOGGER = Logger.getLogger(MainActivity.class.getName());

    private final Subject<ActivityResult> activityResultSubject = PublishSubject.create();
    private final Subject<RequestPermissionResult> requestPermissionsResultSubject = PublishSubject.create();

    private Button playButton;
    private Button createQuestButton;
    private Button passedQuestsButton;
    private Button googleConnectButton;
    private Button facebookConnectButton;
    private CompositeDisposable disposables;

    private AccountPicker accountPicker;
    private PermissionChecker permissionChecker;

    private final String[] locationPermissions = new String[]{
        android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        disposables = new CompositeDisposable();

        playButton = (Button) findViewById(R.id.play_button);
        createQuestButton = (Button) findViewById(R.id.createQuestButton);
        passedQuestsButton = (Button) findViewById(R.id.passedQuestsButton);
        googleConnectButton = (Button) findViewById(R.id.googleConnectButton);
        facebookConnectButton = (Button) findViewById(R.id.facebookConnectButton);

        accountPicker = new AccountPicker(new AccountManagerWrapper(), this, activityResultSubject);

        permissionChecker = new PermissionChecker(this, requestPermissionsResultSubject);

    }

    @Override
    protected void onStart() {
        super.onStart();
        initButtons();
    }

    @Override
    protected void onStop() {
        super.onStop();
        disposables.clear();
    }

    private void initButtons() {
        createOnClickListener(playButton)
                .compose(permissionChecker.<View>applyCheckPermissions(locationPermissions))
                .subscribe(
                        new Consumer<View>() {
                            @Override
                            public void accept(View nill) throws Exception {
                                Intent intent = new Intent(MainActivity.this, CreateQuestActivity.class);
                                MainActivity.this.startActivity(intent);
                            }
                        }
                );

        createOnClickListener(createQuestButton)
                .subscribe(
                        new Consumer<View>() {
                            @Override
                            public void accept(View view) throws Exception {
                                Intent intent = new Intent(MainActivity.this, CreateQuestActivity.class);
                                MainActivity.this.startActivity(intent);
                            }
                        }
                );


        createOnClickListener(passedQuestsButton)
                .subscribe(
                        new Consumer<View>() {
                            @Override
                            public void accept(View view) throws Exception {
                                Intent intent = new Intent(MainActivity.this, PassedQuestsActivity.class);
                                MainActivity.this.startActivity(intent);
                            }
                        }
                );

        createOnClickListener(googleConnectButton)
                .compose(accountPicker.<View>applyPickAccount(AccountType.GOOGLE))
                .subscribe(createAccountObserver());

        createOnClickListener(facebookConnectButton)
                .compose(accountPicker.<View>applyPickAccount(AccountType.FACEBOOK))
                .subscribe(createAccountObserver());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        activityResultSubject.onNext(new ActivityResult(requestCode, resultCode, data));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        requestPermissionsResultSubject.onNext(new RequestPermissionResult(requestCode, permissions, grantResults));
    }

    private Observable<View> createOnClickListener(View view) {
        return new DisablingListener(view)
                .compose(this.<View>applyDisposableCollector());
    }

    private Observer<Account> createAccountObserver() {
        return new Observer<Account>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Account account) {
                LOGGER.warning("auth success" + account.toString());
            }

            @Override
            public void onError(Throwable t) {
                LOGGER.log(Level.WARNING, "auth failed", t);
            }

            @Override
            public void onComplete() {
            }
        };
    }

    private <E> ObservableTransformer<E, E> applyDisposableCollector() {
        return new ObservableTransformer<E, E>() {
            @Override
            public ObservableSource<E> apply(Observable<E> upstream) {
                return upstream
                        .doOnSubscribe(
                                new Consumer<Disposable>() {
                                    @Override
                                    public void accept(Disposable disposable) throws Exception {
                                        disposables.add(disposable);
                                    }
                                }
                        );
            }
        };
    }
}
