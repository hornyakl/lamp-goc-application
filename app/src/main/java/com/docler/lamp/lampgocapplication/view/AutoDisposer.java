package com.docler.lamp.lampgocapplication.view;

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.operators.single.SingleAmb;
import io.reactivex.internal.operators.single.SingleMap;
import io.reactivex.plugins.RxJavaPlugins;

public class AutoDisposer<T> implements SingleTransformer<T, T> {
    private final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    public SingleSource<T> apply(Single<T> upstream) {
//        return Single.using(
//                () -> {
//                    return new AtomicReference<Disposable>();
//                },
//                (AtomicReference<Disposable> wrapper) -> {
//                    return upstream.doOnSubscribe(
//                            (Disposable disposable) -> {
//                                wrapper.set(disposable);
//                                disposables.add(disposable);
//                            }
//                    );
//                },
//                (AtomicReference<Disposable> wrapper) -> {
//                    Disposable disposable = wrapper.getAndSet(null);
//                    if (disposable != null) {
//                        disposables.remove(disposable);
//                    }
//                },
//                false
//        );

        upstream.compose(
                new SingleTransformer<T, T>() {
                    @Override
                    public SingleSource<T> apply(Single<T> nil) {
                        return new AutoDisposerSingle<T>(nil, disposables);
                    }
                }
        );

        return new AutoDisposerSingle<>(upstream, disposables);

//        return upstream.lift(
//                (SingleObserver<? super T> observer) -> {
//                    return new DoOnDisposeFinallyObserver<>(
//                            observer,
//                            (d) -> {
//                                disposables.add(d);
//                            },
//                            (d) -> {
//                                disposables.remove(d);
//                            }
//                    );
//                }
//        );
    }

    public void clear() {
        disposables.clear();
    }

    private static final class AutoDisposerSingle<T> extends Single<T> {
        private final SingleSource<? extends T> source;
        private final CompositeDisposable disposables;

        public AutoDisposerSingle(SingleSource<? extends T> source, CompositeDisposable disposables) {
            this.source = source;
            this.disposables = disposables;
        }

        @Override
        protected void subscribeActual(SingleObserver<? super T> observer) {
            source.subscribe(new DoOnDisposeFinallyObserver2(observer));
        }

        private final class DoOnDisposeFinallyObserver2
                implements SingleObserver<T>, Disposable {

            final SingleObserver<? super T> actual;

            Disposable d;

            DoOnDisposeFinallyObserver2(
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




    private static final class DoOnDisposeFinallyObserver<T>
            implements SingleObserver<T>, Disposable {

        final SingleObserver<? super T> actual;

        Disposable d;

        private final AtomicReference<Consumer<Disposable>> onSubscribeAtomic;
        private final AtomicReference<Consumer<Disposable>> onFinallyAtomic;

        DoOnDisposeFinallyObserver(
                SingleObserver<? super T> actual,
                Consumer<Disposable> onSubscribe,
                Consumer<Disposable> onFinally) {
            this.actual = actual;
            onSubscribeAtomic = new AtomicReference<>();
            onFinallyAtomic = new AtomicReference<>();

            onSubscribeAtomic.lazySet(onSubscribe);
            onFinallyAtomic.lazySet(onFinally);
        }

        @Override
        public void dispose() {
            d.dispose();
            runFinally();
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

                Consumer<Disposable> onSubscribe = onSubscribeAtomic.getAndSet(null);
                if (onSubscribe != null) {
                    try {
                        onSubscribe.accept(d);
                    } catch (Throwable ex) {
                        Exceptions.throwIfFatal(ex);
                        RxJavaPlugins.onError(ex);
                    }
                }
            }
        }

        @Override
        public void onSuccess(T value) {
            actual.onSuccess(value);
            runFinally();
        }

        @Override
        public void onError(Throwable e) {
            actual.onError(e);
            runFinally();
        }

        private void runFinally() {
            Consumer<Disposable> onFinally = onFinallyAtomic.getAndSet(null);
            if (onFinally != null) {
                try {
                    onFinally.accept(d);
                } catch (Throwable ex) {
                    Exceptions.throwIfFatal(ex);
                    RxJavaPlugins.onError(ex);
                }
            }
        }
    }
}
