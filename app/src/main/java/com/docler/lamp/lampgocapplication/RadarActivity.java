package com.docler.lamp.lampgocapplication;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.docler.lamp.lampgocapplication.Quest.Quest;

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
    protected void updateEulerAngles(double x, double y, double z) {
        drawView.setAngle(x);
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
