package com.docler.lamp.lampgocapplication.view;

import android.view.View;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.functions.Functions;

public class DisablingCompletableListener implements View.OnClickListener {

    private final Completable completable;
    private final Action onComplete;
    private final Consumer<? super Throwable> onError;

    public DisablingCompletableListener(Completable completable) {
        this(
                completable,
                Functions.EMPTY_ACTION,
                Functions.ERROR_CONSUMER
        );
    }

    public DisablingCompletableListener(Completable completable, Action onComplete) {
        this(
                completable,
                onComplete,
                Functions.ERROR_CONSUMER
        );
    }

    public DisablingCompletableListener(
            Completable completable,
            Action onComplete,
            Consumer<? super Throwable> onError) {
        this.completable = completable;
        this.onComplete = onComplete;
        this.onError = onError;
    }

    @Override
    public void onClick(final View view) {
        view.setEnabled(false);

        completable
                .observeOn(
                        AndroidSchedulers.mainThread()
                )
                .doFinally(
                        new Action() {
                            @Override
                            public void run() throws Exception {
                                view.setEnabled(false);
                            }
                        }
                )
                .subscribe(
                        onComplete,
                        onError
                )
        ;
    }
}
