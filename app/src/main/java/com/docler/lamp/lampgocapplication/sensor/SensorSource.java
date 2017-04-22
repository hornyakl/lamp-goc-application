package com.docler.lamp.lampgocapplication.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.docler.lamp.lampgocapplication.utils.rxutils.RefCountObservableTransformer;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.functions.Action;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class SensorSource extends Observable<SensorEvent> implements SensorEventListener {

    private final SensorManager sensorManager;
    private final Sensor sensor;

    private final Subject<SensorEvent> delegateSubject;
    private final Observable<SensorEvent> delegateObservable;

    public SensorSource(SensorManager sensorManager, Sensor sensor) {
        this.sensorManager = sensorManager;
        this.sensor = sensor;

        delegateSubject = PublishSubject.create();

        delegateObservable = delegateSubject
                .compose(new RefCountObservableTransformer<SensorEvent>(
                        new Action() {
                            @Override
                            public void run() throws Exception {
                                start();
                            }
                        },
                        new Action() {
                            @Override
                            public void run() throws Exception {
                                stop();
                            }
                        }
                ));

    }

    private void start() {
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    private void stop() {
        sensorManager.unregisterListener(this, sensor);
    }


    @Override
    protected void subscribeActual(Observer<? super SensorEvent> observer) {
        delegateObservable.subscribe(observer);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        delegateSubject.onNext(event);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
