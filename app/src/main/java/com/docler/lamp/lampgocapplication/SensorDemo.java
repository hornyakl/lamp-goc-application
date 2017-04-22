package com.docler.lamp.lampgocapplication;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.docler.lamp.lampgocapplication.sensorFusion.orientationProvider.ImprovedOrientationSensor2Provider;
import com.docler.lamp.lampgocapplication.sensorFusion.orientationProvider.OrientationProvider;
import com.docler.lamp.lampgocapplication.matrix.MatrixF4x4;
import com.docler.lamp.lampgocapplication.matrix.Quaternion;
import com.docler.lamp.lampgocapplication.matrix.Vector4f;

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

                Vector4f x = new Vector4f(1, 0, 0, 0);
                Vector4f y = new Vector4f(0, 1, 0, 0);
                Vector4f z = new Vector4f(0, 0, 1, 0);
                Vector4f w = new Vector4f(0, 0, 0, 1);

                matrix.multiplyVector4fByMatrix(x);
                matrix.multiplyVector4fByMatrix(y);
                matrix.multiplyVector4fByMatrix(z);
                matrix.multiplyVector4fByMatrix(w);

                coordinateX.setText("" + Math.atan2(x.getX(), x.getY()) + " " + Math.atan2(y.getX(), y.getY()) + " " + Math.atan2(z.getX(), z.getY()));
                coordinateY.setText("" + Math.atan2(x.getX(), x.getZ()) + " " + Math.atan2(y.getX(), y.getZ()) + " " + Math.atan2(z.getX(), z.getZ()));
                coordinateZ.setText("" + Math.atan2(x.getY(), x.getZ()) + " " + Math.atan2(y.getY(), y.getZ()) + " " + Math.atan2(z.getY(), z.getZ()));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
}
