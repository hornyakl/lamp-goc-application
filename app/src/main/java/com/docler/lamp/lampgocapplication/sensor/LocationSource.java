package com.docler.lamp.lampgocapplication.sensor;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.docler.lamp.lampgocapplication.permission.PermissionChecker;
import com.docler.lamp.lampgocapplication.utils.rxutils.RefCountObservableTransformer;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.functions.Action;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class LocationSource extends Observable<Location> implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000 * 5;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private final GoogleApiClient googleApiClient;
    private final LocationRequest locationRequest;
    private final PermissionChecker permissionChecker;

    private final Subject<Location> delegateSubject;
    private final Observable<Location> delegateObservable;

    private boolean isActive = false;
    private PendingResult<Status> locationUpdateStatus;

    public LocationSource(Context context, PermissionChecker permissionChecker) {
        this.permissionChecker = permissionChecker;

        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        delegateSubject = PublishSubject.create();

        delegateObservable = delegateSubject
                .compose(
                        new RefCountObservableTransformer<Location>(
                                new Action() {
                                    @Override
                                    public void run() throws Exception {
                                        isActive = true;
                                        startLocationUpdates();
                                    }
                                },
                                new Action() {
                                    @Override
                                    public void run() throws Exception {
                                        isActive = false;
                                        stopLocationUpdates();
                                    }
                                }
                        )
                );
    }

    @Override
    protected void subscribeActual(Observer<? super Location> observer) {
        delegateObservable.subscribe(observer);
    }

    @SuppressWarnings({"MissingPermission"})
    protected void startLocationUpdates() {
        if (!isActive || !googleApiClient.isConnected()) {
            return;
        }

        permissionChecker
                .checkPermissions(
                        new String[]{
                                android.Manifest.permission.ACCESS_FINE_LOCATION,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION
                        }
                )
                .subscribe(
                        new Action() {
                            @Override
                            public void run() throws Exception {
                                locationUpdateStatus = LocationServices.FusedLocationApi
                                        .requestLocationUpdates(googleApiClient, locationRequest, LocationSource.this);
                            }
                        }
                );
    }

    protected void stopLocationUpdates() {
        if (locationUpdateStatus == null) {
            return;
        }

        if (!locationUpdateStatus.isCanceled()) {
            locationUpdateStatus.cancel();
        }

        locationUpdateStatus = null;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            return;
        }

        delegateSubject.onNext(location);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        stopLocationUpdates();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
