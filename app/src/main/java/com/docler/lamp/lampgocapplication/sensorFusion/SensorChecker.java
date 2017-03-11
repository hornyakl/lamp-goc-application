package com.docler.lamp.lampgocapplication.sensorFusion;

interface SensorChecker {
    /**
     * Checks if the device that is currently running the application
     * supports the usage of Android's integrated sensor fusion (Rotation vector).
     *
     * @return True, if an integrated sensor fusion (Rotation vector) is available. False otherwise.
     */
    boolean IsRotationVectorAvailable();
}
