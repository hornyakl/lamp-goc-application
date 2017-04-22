package com.docler.lamp.lampgocapplication.view;

import io.reactivex.SingleObserver;
import io.reactivex.SingleOperator;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;

public class DisposableCollectorOperator<T> implements SingleOperator<T, T> {
    private final CompositeDisposable disposables;

    public DisposableCollectorOperator(CompositeDisposable disposables) {
        this.disposables = disposables;
    }

    @Override
    public SingleObserver<? super T> apply(SingleObserver<? super T> observer) throws Exception {
        return new DisposableCollectorObserver(observer);
    }

    private final class DisposableCollectorObserver
            implements SingleObserver<T>, Disposable {

        final SingleObserver<? super T> actual;

        Disposable d;

        DisposableCollectorObserver(
                SingleObserver<? super T> actual
        ) {
            this.actual = actual;
        }

        @Override
        public void dispose() {
            d.dispose();
            disposables.delete(this);
        }

        @Override
        public boolean isDisposed() {
            return d.isDisposed();
        }

        @Override
        public void onSubscribe(Disposable d) {
            if (DisposableHelper.validate(this.d, d)) {
                this.d = d;
                actual.onSubscribe(this);
                disposables.add(this);
            }
        }

        @Override
        public void onSuccess(T value) {
            actual.onSuccess(value);
            disposables.delete(this);
        }

        @Override
        public void onError(Throwable e) {
            actual.onError(e);
            disposables.delete(this);
        }
    }
}
