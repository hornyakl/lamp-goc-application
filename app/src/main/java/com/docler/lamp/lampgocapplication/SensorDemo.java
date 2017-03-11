package com.docler.lamp.lampgocapplication;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.docler.lamp.lampgocapplication.sensorFusion.orientationProvider.OrientationProvider;
import com.docler.lamp.lampgocapplication.sensorFusion.orientationProvider.RotationVectorProvider;

public class SensorDemo extends AppCompatActivity {

    private OrientationProvider orientationProvider;

    private TextView coordinateX;
    private TextView coordinateY;
    private TextView coordinateZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_demo);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        orientationProvider = new RotationVectorProvider(
            sensorManager
        );

        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        sensorManager.registerListener(new SensorDemoSensorListener(), sensor, SensorManager.SENSOR_DELAY_GAME);

        orientationProvider.start();

        coordinateX = (TextView) findViewById(R.id.coordinate_x);
        coordinateY = (TextView) findViewById(R.id.coordinate_y);
        coordinateZ = (TextView) findViewById(R.id.coordinate_z);

    }

    private class SensorDemoSensorListener implements SensorEventListener
    {

        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] angles = new float[3];
            orientationProvider.getEulerAngles(angles);

            coordinateX.setText("" + angles[0]);
            coordinateY.setText("" + angles[1]);
            coordinateZ.setText("" + angles[2]);

            if (Math.abs(angles[1]) < 0.3)
            {
                Intent intent = new Intent(SensorDemo.this, SensorDemo.class);
                startActivity(intent);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
}