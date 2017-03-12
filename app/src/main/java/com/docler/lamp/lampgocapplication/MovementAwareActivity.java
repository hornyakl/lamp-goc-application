package com.docler.lamp.lampgocapplication;

import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.docler.lamp.lampgocapplication.Quest.Quest;
import com.docler.lamp.lampgocapplication.sensorFusion.HardwareChecker;
import com.docler.lamp.lampgocapplication.sensorFusion.orientationProvider.ImprovedOrientationSensor2Provider;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;

public abstract class MovementAwareActivity extends AppCompatActivity {
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000 * 5;

    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private LampApplication application;

    private SensorManager sensorManager;
    private HardwareChecker hardwareChecker;

    private MovementAwareSensorListener sensorListener;

    private MovementAwareLocationListener locationListener;

    private ImprovedOrientationSensor2Provider orientationProvider;

    private GoogleApiClient googleApiClient;

    private LocationRequest locationRequest;

    private boolean isActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application = (LampApplication) getApplication();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        hardwareChecker = new HardwareChecker(sensorManager);
        if (
                !hardwareChecker.IsRotationVectorAvailable()
                        || !hardwareChecker.IsGyroscopeAvailable()
                ) {
            finish();
        }

        sensorListener = new MovementAwareSensorListener();

        locationListener = new MovementAwareLocationListener();

        orientationProvider = new ImprovedOrientationSensor2Provider(
                sensorManager
        );

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(locationListener)
                .addOnConnectionFailedListener(locationListener)
                .addApi(LocationServices.API)
                .build();

        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        application.getQuestProvider().registerQuestListener(new QuestProviderListener());
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        if (
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    666
            );

            return;
        }

        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationListener);
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, locationListener);
    }

    @Override
    protected void onPause() {
        application.stopViewChangeListen();
        sensorManager.unregisterListener(sensorListener);
        orientationProvider.stop();

        stopLocationUpdates();

        super.onPause();

        isActive = false;
    }

    @Override
    protected void onResume() {
        isActive = true;

        super.onResume();

        orientationProvider.start();

        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_GAME);

        startLocationUpdates();

        application.startViewChangeListen(this);
    }

    protected abstract void updateEulerAngles(double x, double y, double z);

    protected abstract void updateLocation(double latitude, double longitude);

    protected abstract void onQuests(List<Quest> quests);

    private class MovementAwareSensorListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {

            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                float[] angles = new float[3];
                orientationProvider.getEulerAngles(angles);

                updateEulerAngles(angles[0], angles[1], angles[2]);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    private class MovementAwareLocationListener implements
            LocationListener,
            GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener {

        @Override
        public void onLocationChanged(Location location) {
            if (location == null) {
                return;
            }

            updateLocation(location.getLatitude(), location.getLongitude());
        }


        @Override
        public void onConnected(@Nullable Bundle bundle) {
            if (isActive) {
                startLocationUpdates();
            }
        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }
    }

    private class QuestProviderListener implements QuestProvider.QuestListener {

        @Override
        public void onQuests(List<Quest> quests) {
            MovementAwareActivity.this.onQuests(quests);
        }
    }
}
