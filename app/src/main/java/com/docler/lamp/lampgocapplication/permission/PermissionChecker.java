package com.docler.lamp.lampgocapplication.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.MaybeSource;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public class PermissionChecker {

    private final Activity activity;

    private final Observable<RequestPermissionResult> requestPermission;

    public PermissionChecker(Activity activity, Observable<RequestPermissionResult> requestPermission) {
        this.activity = activity;
        this.requestPermission = requestPermission;
    }

    public <T> ObservableTransformer<T, T> applyCheckPermissions(final String[] permissions) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream
                        .flatMapSingle(
                                new Function<T, SingleSource<T>>() {
                                    @Override
                                    public SingleSource<T> apply(T value) throws Exception {
                                        return checkPermissions(permissions)
                                                .toSingleDefault(value);
                                    }
                                }
                        )
                        .retry();
            }
        };
    }

    public Completable checkPermissions(String[] permissions) {
        return Single
                .just(permissions)
                // filter out permissions already granted
                .map(
                        new Function<String[], List<String>>() {
                            @Override
                            public List<String> apply(String[] permissions) throws Exception {
                                List<String> permissionsToCheck = new ArrayList<>();
                                for (String permission : permissions) {
                                    if (
                                            ActivityCompat.checkSelfPermission(
                                                    activity,
                                                    permission
                                            ) != PackageManager.PERMISSION_GRANTED) {
                                        permissionsToCheck.add(permission);
                                    }
                                }

                                return permissionsToCheck;
                            }
                        }
                )
                // complete if all permissions are already granted
                .filter(
                        new Predicate<List<String>>() {
                            @Override
                            public boolean test(List<String> permissionsToCheck) throws Exception {
                                return !permissionsToCheck.isEmpty();
                            }
                        }
                )
                // 1. Return a Maybe<RequestPermissionResult> for the List<String> permissionsToCheck
                // 2. flatMap will subscribe to this Maybe
                // 3. doOnSubscribe is called, an android permission request is fired
                // 4. user allows or denys permission
                // 5. in the activity code, RequestPermissionResult is added to requestPermission (which is really a subject)
                // 6. requestPermissionResult emits the item
                // 7. requestCode filter is called to skip results coming for other permission requests
                // 8. firstElement is called to convert the Observable into a Maybe
                .flatMap(
                        new Function<List<String>, MaybeSource<RequestPermissionResult>>() {
                            @Override
                            public MaybeSource<RequestPermissionResult> apply(final List<String> permissionsToCheck) throws Exception {
                                final int requestCode = RequestCodeProvider.getCode();

                                return requestPermission
                                        .doOnSubscribe(
                                                new Consumer<Disposable>() {
                                                    @Override
                                                    public void accept(Disposable d) throws Exception {
                                                        ActivityCompat.requestPermissions(
                                                                activity,
                                                                permissionsToCheck.toArray(new String[permissionsToCheck.size()]),
                                                                requestCode
                                                        );
                                                    }
                                                }
                                        )
                                        .filter(
                                                new Predicate<RequestPermissionResult>() {
                                                    @Override
                                                    public boolean test(RequestPermissionResult requestPermissionResult) throws Exception {
                                                        return requestPermissionResult.getRequestCode() == requestCode;
                                                    }
                                                }
                                        )
                                        .firstElement();
                            }
                        }
                )
                .map(
                        new Function<RequestPermissionResult, Boolean>() {
                            @Override
                            public Boolean apply(RequestPermissionResult result) throws Exception {
                                if (result.getGrantResults().length < 1) {
                                    throw new PermissionException();
                                }

                                for (int grantResult : result.getGrantResults()) {
                                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                                        throw new PermissionException();
                                    }
                                }

                                return true;
                            }
                        }
                )
                .ignoreElement();
    }
}
