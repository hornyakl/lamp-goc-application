package com.docler.lamp.lampgocapplication.ui.questLocator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.docler.lamp.lampgocapplication.quest.RelativeQuestPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RadarView extends View {

    private static final double PRE_MULTI = 400.d;
    private static final double POST_MULTI = 300.d;

    int midX;
    int midY;
    int dotRadius;

    private Collection<RelativeQuestPosition> questPositions;

    Paint paint = null;

    public RadarView(Context context) {
        super(context);
        paint = new Paint();

        dotRadius = 20;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        midX = getWidth() / 2;
        midY = getHeight() / 2;
    }

    public void setQuestPositions(Collection<RelativeQuestPosition> questPositions) {
        this.questPositions = questPositions;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Use Color.parseColor to define HTML colors
        paint.setColor(Color.parseColor("#CD5C5C"));
        canvas.drawCircle(midX, midY, dotRadius, paint);

        paint.setColor(Color.YELLOW);
        for (RelativeQuestPosition questPosition : questPositions) {
            double angle = questPosition.getAngle();
            double distance = questPosition.getDistance();

            double pixelDistance = Math.atan(distance * PRE_MULTI) * POST_MULTI;

            canvas.drawCircle(midX + getX(angle, pixelDistance), getY(angle, pixelDistance), dotRadius, paint);
        }
    }

    private float getX(double angle, double distance) {
        return (float)(Math.sin(angle) * distance);
    }

    private float getY(double angle, double distance) {
        return (float)(Math.cos(angle) * distance);
    }
}
