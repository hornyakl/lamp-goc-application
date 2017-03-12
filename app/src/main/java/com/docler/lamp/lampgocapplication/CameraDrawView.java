package com.docler.lamp.lampgocapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class CameraDrawView extends View {

    private static final double ANGLE = Math.PI / 2;
    private static final double POST_MULTI = 500;

    int midX;
    int midY;
    int dotRadius;

    private List<double[]> points;

    private double ownLatitude;
    private double ownLongitude;

    private double ownAngle;

    Paint paint = null;

    public CameraDrawView(Context context) {
        super(context);
        paint = new Paint();

        dotRadius = 20;

        points = new ArrayList<>();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        midX = getWidth() / 2;
        midY = getHeight() / 2;
    }

    public void clearPoints()
    {
        points.clear();
    }

    public void addPoint(double latitude, double longitude) {
        clearPoints();
        points.add(new double[]{latitude, longitude});
        invalidate();
    }

    public void setLocation(double latitude, double longitude) {
        ownLatitude = latitude;
        ownLongitude = longitude;
        invalidate();
    }

    public void setAngle(double angle)
    {
        ownAngle = angle;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Use Color.parseColor to define HTML colors
        paint.setColor(Color.parseColor("#CD5C5C"));
        canvas.drawCircle(midX, midY, dotRadius, paint);

        paint.setColor(Color.YELLOW);
        for (double[] point : points) {
            int drawY = getOffset(point[0], point[1]);

            if (drawY == Integer.MIN_VALUE)
            {
                continue;
            }

            canvas.drawCircle(midX, midY + drawY, dotRadius, paint);
        }
    }

    private int getOffset(double latitude, double longitude) {
        double longDif = longitude - ownLongitude; // x
        double latDif = latitude - ownLatitude; // y

//        double distance = Math.sqrt(longDif * longDif + latDif * latDif);

        double angle = Math.atan2(latDif, longDif);

        double correctedAngle = toNonNegativeAngle(angle);
        double correctedOwnAngle = toNonNegativeAngle(ownAngle);
        double minAngle = correctedOwnAngle- ANGLE / 2;
        double maxAngle = correctedOwnAngle + ANGLE / 2;

        if (correctedAngle > minAngle
                && correctedAngle < maxAngle)
        {
            return (int) (Math.sin(correctedAngle - correctedOwnAngle) * POST_MULTI);
        }
        return Integer.MIN_VALUE;
    }

    private double toNonNegativeAngle(double angle)
    {
        return (angle % (Math.PI * 2)) + Math.PI * 2;
    }
}
