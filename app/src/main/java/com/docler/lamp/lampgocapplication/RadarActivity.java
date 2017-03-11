package com.docler.lamp.lampgocapplication;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.docler.lamp.lampgocapplication.Quest.Quest;
import com.docler.lamp.lampgocapplication.sensorFusion.orientationProvider.OrientationProvider;
import com.docler.lamp.lampgocapplication.sensorFusion.orientationProvider.RotationVectorProvider;

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

    private static final int MIN_TIME_BW_UPDATES = 1000 * 5;
    private static final int MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;

    private JSONObject questList;
    private String questListJson;
    private String questListPath = "https://goc-lamp.tk/quest-list";

    private LampApplication application;

    private RadarDrawView drawView;


    private SensorManager sensorManager;
    private LocationManager locationManager;

    private ViewChangerSensorListener sensorListener;

    private RadarLocationListener locationListener;

    private OrientationProvider orientationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radar);

        application = (LampApplication) getApplication();


        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorListener = new ViewChangerSensorListener();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new RadarLocationListener();

        orientationProvider = new RotationVectorProvider(
                sensorManager
        );


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
        locationManager.removeUpdates(locationListener);

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        orientationProvider.start();

        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_GAME);

        registerLocationListener();

        application.startViewChangeListen(this);
    }

    private void registerLocationListener() {
        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        boolean isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);


        // Add permission for gps and let user grant the permission
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    777
            );

            return;
        }

        if (isNetworkEnabled) {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES,
                    locationListener
            );

            locationListener.onLocationChanged(locationManager
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            );
        }

        // if GPS Enabled get lat/long using GPS Services
        if (isGPSEnabled) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES,
                    locationListener
            );

            if (locationManager != null) {
                locationListener.onLocationChanged(locationManager
                        .getLastKnownLocation(LocationManager.GPS_PROVIDER)
                );
            }
        }
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

    private class RadarLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            if (location == null)
            {
                return;
            }

            drawView.setLocation(location.getLatitude(), location.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

}
