package com.docler.lamp.lampgocapplication.view;

import android.view.View;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class DisablingListener extends Observable<View> implements View.OnClickListener {

    private final Observer<View> observer;
    private final Observable<View> observable;

    public DisablingListener(View view) {
        Subject<View> subject = PublishSubject.create();

        observable = subject
                .observeOn(
                        AndroidSchedulers.mainThread()
                )
                .doOnNext(
                        new Consumer<View>() {
                            @Override
                            public void accept(View view) throws Exception {
                                view.setEnabled(false);
                            }
                        }
                )
                .doAfterNext(
                        new Consumer<View>() {
                            @Override
                            public void accept(View view) throws Exception {
                                view.setEnabled(true);
                            }
                        }
                );

        observer = subject;

        view.setOnClickListener(this);
    }

    @Override
    public void onClick(final View view) {
        observer.onNext(view);
    }

    @Override
    protected void subscribeActual(Observer<? super View> observer) {
        observable.subscribe(observer);
    }
}
