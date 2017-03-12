package com.docler.lamp.lampgocapplication;

import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.docler.lamp.lampgocapplication.Quest.Quest;
import com.docler.lamp.lampgocapplication.sensorFusion.orientationProvider.OrientationProvider;
import com.docler.lamp.lampgocapplication.sensorFusion.orientationProvider.RotationVectorProvider;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;

public class RadarActivity extends AppCompatActivity {

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000 * 5;

    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private JSONObject questList;
    private String questListJson;
    private String questListPath = "https://goc-lamp.tk/quest-list";

    private LampApplication application;

    private RadarDrawView drawView;

    private SensorManager sensorManager;

    private ViewChangerSensorListener sensorListener;

    private RadarLocationListener locationListener;

    private OrientationProvider orientationProvider;

    private GoogleApiClient googleApiClient;

    private LocationRequest locationRequest;

    private boolean isActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radar);

        application = (LampApplication) getApplication();


        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorListener = new ViewChangerSensorListener();

        locationListener = new RadarLocationListener();

        orientationProvider = new RotationVectorProvider(
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

        FrameLayout frame = (FrameLayout) findViewById(R.id.radar_frame);

        drawView = new RadarDrawView(this);

        frame.addView(drawView);

        AsyncTask<Void, Void, Void> mTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    questListJson = getQuestListFromServer(questListPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);

                try {
                    questList = new JSONObject(questListJson);

                    Iterator<String> keys = questList.keys();

                    ArrayList<Quest> quests = new ArrayList<>(questList.length());
                    drawView.clearPoints();

                    while (keys.hasNext()) {
                        String id = keys.next();
                        JSONObject questObject = questList.getJSONObject(id);
                        Quest quest = new Quest(
                                questObject.getInt("id"),
                                questObject.getString("name"),
                                questObject.getString("description"),
                                questObject.getDouble("latitude"),
                                questObject.getDouble("longitude"),
                                questObject.getLong("experience_point")
                        );

                        quests.add(
                                quest
                        );

                        drawView.addPoint(quest.getLatitude(), quest.getLongitude());
                    }


                    Toast.makeText(RadarActivity.this, "ejjjha", Toast.LENGTH_LONG);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        mTask.execute();
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

    private static String getQuestListFromServer(String url) throws IOException {
        BufferedReader inputStream;

        URL jsonUrl = new URL(url);
        URLConnection dc = jsonUrl.openConnection();

        dc.setConnectTimeout(1000);
        dc.setReadTimeout(1000);

        inputStream = new BufferedReader(
                new InputStreamReader(
                        dc.getInputStream()
                )
        );

        return inputStream.readLine();
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

    private class ViewChangerSensorListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {

            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                float[] angles = new float[3];
                orientationProvider.getEulerAngles(angles);
                float angle = angles[0];

                drawView.setAngle(angle);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    private class RadarLocationListener implements
            LocationListener,
            GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener {

        @Override
        public void onLocationChanged(Location location) {
            if (location == null) {
                return;
            }

            drawView.setLocation(location.getLatitude(), location.getLongitude());
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

}
