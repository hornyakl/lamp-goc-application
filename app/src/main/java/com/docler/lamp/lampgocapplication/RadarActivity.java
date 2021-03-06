package com.docler.lamp.lampgocapplication;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.docler.lamp.lampgocapplication.Quest.Quest;
import com.docler.lamp.lampgocapplication.sensorFusion.orientationProvider.ImprovedOrientationSensor2Provider;

import java.util.List;

public class RadarActivity extends MovementAwareActivity {

    private RadarDrawView drawView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radar);

        FrameLayout frame = (FrameLayout) findViewById(R.id.radar_frame);

        getSupportActionBar().hide();

        drawView = new RadarDrawView(this);

        frame.addView(drawView);
    }

    @Override
    protected void updateOrientation(ImprovedOrientationSensor2Provider orientationProvider) {
        float[] angles = new float[3];
        orientationProvider.getEulerAngles(angles);

        drawView.setAngle(angles[0]);
    }

    @Override
    protected void updateLocation(double latitude, double longitude) {
        drawView.setLocation(latitude, longitude);
    }

    @Override
    protected void onQuests(List<Quest> quests) {
        drawView.clearPoints();
        for (Quest quest : quests) {
            drawView.addPoint(quest.getLatitude(), quest.getLongitude());
        }
    }


}
