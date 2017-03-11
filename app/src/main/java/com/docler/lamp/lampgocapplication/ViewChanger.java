package com.docler.lamp.lampgocapplication;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.docler.lamp.lampgocapplication.sensorFusion.orientationProvider.OrientationProvider;

public class ViewChanger
{
    private Activity activeActivity;

    private OrientationProvider orientationProvider;



//    public ViewChanger() {
//
//        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//
//        orientationProvider = new RotationVectorProvider(
//                sensorManager
//        );
//
//        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
//
//        sensorManager.registerListener(new SensorDemo.SensorDemoSensorListener(), sensor, SensorManager.SENSOR_DELAY_GAME);
//
//        orientationProvider.start();
//    }

    public void setActiveActivity(Activity activeActivity) {
        this.activeActivity = activeActivity;
    }


    private class ViewChangerSensorListener implements SensorEventListener
    {

        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] angles = new float[3];
            orientationProvider.getEulerAngles(angles);

//            coordinateX.setText("" + angles[0]);
//            coordinateY.setText("" + angles[1]);
//            coordinateZ.setText("" + angles[2]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

}
