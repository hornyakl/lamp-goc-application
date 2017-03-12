package com.docler.lamp.lampgocapplication;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.docler.lamp.lampgocapplication.sensorFusion.orientationProvider.ImprovedOrientationSensor2Provider;
import com.docler.lamp.lampgocapplication.sensorFusion.orientationProvider.OrientationProvider;
import com.docler.lamp.lampgocapplication.sensorFusion.orientationProvider.RotationVectorProvider;
import com.docler.lamp.lampgocapplication.sensorFusion.representation.MatrixF4x4;
import com.docler.lamp.lampgocapplication.sensorFusion.representation.Quaternion;
import com.docler.lamp.lampgocapplication.sensorFusion.representation.Vector4f;

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

        orientationProvider = new ImprovedOrientationSensor2Provider(
            sensorManager
        );

        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        sensorManager.registerListener(new SensorDemoSensorListener(), sensor, SensorManager.SENSOR_DELAY_NORMAL);

        orientationProvider.start();

        coordinateX = (TextView) findViewById(R.id.coordinate_x);
        coordinateY = (TextView) findViewById(R.id.coordinate_y);
        coordinateZ = (TextView) findViewById(R.id.coordinate_z);

    }

    private long lastTime = 0;

    private class SensorDemoSensorListener implements SensorEventListener
    {

        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] angles = new float[3];
            orientationProvider.getEulerAngles(angles);

            if (lastTime != System.currentTimeMillis() / 1000)
            {

                lastTime = System.currentTimeMillis() / 1000;
                Quaternion quaternion = new Quaternion();
                orientationProvider.getQuaternion(quaternion);

                MatrixF4x4 matrix = new MatrixF4x4();
                orientationProvider.getRotationMatrix(matrix);
                matrix.getMatrix();

                coordinateX.setText("" + angles[0] + " " + event.values[0]);
                coordinateY.setText("" + angles[1] + " " + event.values[1]);
                coordinateZ.setText("" + angles[2] + " " + event.values[2]);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
}
