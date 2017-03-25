package com.docler.lamp.lampgocapplication;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.docler.lamp.lampgocapplication.quest.Quest;
import com.docler.lamp.lampgocapplication.sensorFusion.orientationProvider.OrientationProvider;
import com.docler.lamp.lampgocapplication.sensorFusion.orientationProvider.RotationVectorProvider;
import com.docler.lamp.lampgocapplication.utils.HttpSingles;
import com.docler.lamp.lampgocapplication.utils.JsonFunctions;

public class LampApplication extends Application {

    private SensorManager sensorManager;

    private ViewChangerSensorListener sensorListener;

    private Activity currentActivity;

    private OrientationProvider orientationProvider;

    private QuestProvider questProvider;

    private static final float CAMERA_VIEW_TILT = 0.7f;
    private static final float RADAR_VIEW_TILT = 0.5f;
    private static final float MIDDLE_TILT = 0.6f;

    public Quest currentQuest;

    @Override
    public void onCreate() {
        super.onCreate();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorListener = new ViewChangerSensorListener();

        orientationProvider = new RotationVectorProvider(
            sensorManager
        );

        questProvider = new QuestProvider(new JsonFunctions(), new HttpSingles());
    }

    public void startViewChangeListen(Activity currentActivity) {
        this.currentActivity = currentActivity;
        orientationProvider.start();
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    public void stopViewChangeListen() {
        this.currentActivity = null;
        sensorManager.unregisterListener(sensorListener);
        orientationProvider.stop();
    }

    public QuestProvider getQuestProvider() {
        return questProvider;
    }

    public <T extends Activity> void changeActivity(Class<T> clazz) {
        stopViewChangeListen();
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    private class ViewChangerSensorListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                float[] angles = new float[3];
                orientationProvider.getEulerAngles(angles);
                float tilt = Math.abs(angles[1]);

                if (currentActivity instanceof RadarActivity) {
                    if (tilt > CAMERA_VIEW_TILT) {
                        changeActivity(OldCameraActivity.class);
                    }
                } else if (currentActivity instanceof OldCameraActivity) {
                    if (tilt < RADAR_VIEW_TILT) {
                        changeActivity(RadarActivity.class);
                    }
                } else {
                    if (tilt > MIDDLE_TILT) {
                        changeActivity(OldCameraActivity.class);
                    } else {
                        changeActivity(RadarActivity.class);
                    }
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
}
