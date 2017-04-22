package com.docler.lamp.lampgocapplication.utils.rxutils;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class RefCountObservableTransformer<E> implements ObservableTransformer<E, E> {

    private final Action start;
    private final Action stop;

    public RefCountObservableTransformer(Action start, Action stop) {
        this.start = start;
        this.stop = stop;
    }

    @Override
    public ObservableSource<E> apply(Observable<E> upstream) {
        return upstream
                .doOnSubscribe(
                        new Consumer<Disposable>() {
                            @Override
                            public void accept(Disposable disposable) throws Exception {
                                start.run();
                            }
                        }
                )
                .doOnDispose(
                        stop
                )
                .replay(1)
                .refCount();
    }
}
