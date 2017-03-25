package com.docler.lamp.lampgocapplication;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import com.docler.lamp.lampgocapplication.quest.Quest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CameraDrawView extends View {

    private static final double ANGLE = Math.PI / 4;
    private static final double POST_MULTI = 1000;

    int midX;
    int midY;
    int dotRadius;

    private List<Quest> points;

    private Map<Quest, int[]> questPositions;

    private double ownLatitude;
    private double ownLongitude;

    private double ownAngle;

    Paint paint = null;

    Bitmap questionMark;

    public CameraDrawView(Context context) {
        super(context);
        paint = new Paint();

        dotRadius = 50;

        points = new ArrayList<>();
        questPositions = new HashMap<>();

        Resources res = context.getResources();
        questionMark = BitmapFactory.decodeResource(res,
                R.drawable.question_mark);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        midX = getWidth() / 2;
        midY = getHeight() / 2;
    }

    public void clearPoints() {
        points.clear();
    }

    public void addPoint(Quest quest) {
        points.add(quest);
        invalidate();
    }

    public void setLocation(double latitude, double longitude) {
        ownLatitude = latitude;
        ownLongitude = longitude;
        invalidate();
    }

    public void setAngle(double angle) {
        ownAngle = angle;
        invalidate();
    }

    public Quest getQuestAt(int x, int y) {
        double minDistanceSqrt = Integer.MAX_VALUE;
        Quest minDistanceQuest = null;

        for (Map.Entry<Quest, int[]> entry : questPositions.entrySet()) {
            int xDif = entry.getValue()[0] - x;
            int yDif = entry.getValue()[1] - y;

            int distanceSqrt = xDif * xDif + yDif * yDif;

            if (distanceSqrt < 10000 && distanceSqrt < minDistanceSqrt) {
                minDistanceSqrt = distanceSqrt;
                minDistanceQuest = entry.getKey();
            }
        }

        return minDistanceQuest;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setColor(Color.YELLOW);
        questPositions.clear();
        for (Quest quest : points) {
            int size = getSize(quest.getLatitude(), quest.getLongitude());
            if (size == 0) {
                continue;
            }

            int xOffset = getOffset(quest.getLatitude(), quest.getLongitude());

            if (xOffset == Integer.MIN_VALUE) {
                continue;
            }

            int drawX = (int) (midX + xOffset);

            questPositions.put(quest, new int[]{drawX, midY});

            canvas.drawBitmap(questionMark, null, new RectF(
                            (int) (drawX - size),
                            (int) (midY - size * 1.4),
                            (int) (drawX + size),
                            (int) (midY + size * 1.4)
                    ),
                    null);

            paint.setTextSize(size);

            canvas.drawText(quest.getName(), drawX - paint.measureText(quest.getName()) / 2, (int)(midY + size * 2), paint);
        }
    }

    private int getSize(double latitude, double longitude) {
        float distanceMeter = distFrom(latitude, longitude, ownLatitude, ownLongitude);
        if (distanceMeter > 100) {
            return 0;
        }

        return 100 - (int) distanceMeter;
    }

    private int getOffset(double latitude, double longitude) {

        double longDif = longitude - ownLongitude; // x
        double latDif = latitude - ownLatitude; // y

        double angle = Math.atan2(latDif, longDif) - Math.PI / 2;

        double correctedAngle = toNonNegativeAngle(angle);
        double correctedOwnAngle = toNonNegativeAngle(ownAngle);
        double minAngle = correctedOwnAngle - ANGLE / 2;
        double maxAngle = correctedOwnAngle + ANGLE / 2;

        if (correctedAngle > minAngle
                && correctedAngle < maxAngle) {
            return (int) ((correctedOwnAngle - correctedAngle) * POST_MULTI);
        }
        return Integer.MIN_VALUE;
    }

    private double toNonNegativeAngle(double angle) {
        return (angle % (Math.PI * 2)) + Math.PI * 2;
    }

    private static float distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (float) (earthRadius * c);
    }
}
