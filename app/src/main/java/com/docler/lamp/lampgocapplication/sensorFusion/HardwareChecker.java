package com.docler.lamp.lampgocapplication.sensorFusion;

import android.hardware.Sensor;
import android.hardware.SensorManager;

/**
 * Tests availability of certain hardware sensors
 * needed for a proper sensor fusion experience.
 *
 * Currently these sensors can be checked for availability:
 *     - Gyroscope
 *     - Accelerometer
 *     - Magnetic field receiver (compass)
 */
public class HardwareChecker implements SensorChecker
{
    private boolean rotationVectorIsAvailable = false;

    public HardwareChecker (SensorManager sensorManager)
    {
        if (sensorManager.getSensorList(Sensor.TYPE_ROTATION_VECTOR).size() > 0)
        {
            rotationVectorIsAvailable = true;
        }
    }

    @Override
    public boolean IsRotationVectorAvailable()
    {
        return rotationVectorIsAvailable;
    }
}
