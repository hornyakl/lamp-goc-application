package com.docler.lamp.lampgocapplication.ui.questLocator;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import com.docler.lamp.lampgocapplication.R;
import com.docler.lamp.lampgocapplication.quest.Quest;
import com.docler.lamp.lampgocapplication.quest.RelativeQuestPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CameraView extends View {

    private static final double ANGLE = Math.PI / 4;
    private static final double POST_MULTI = 1000;

    int midX;
    int midY;
    int dotRadius;

    private Collection<RelativeQuestPosition> questPositions = new ArrayList<>();

    private Map<Quest, int[]> questRenderedPositions = new HashMap<>();

    Paint paint = null;

    Bitmap questionMark;

    public CameraView(Context context) {
        super(context);
        paint = new Paint();

        dotRadius = 50;

        Resources res = context.getResources();
        questionMark = BitmapFactory.decodeResource(res,
                R.drawable.question_mark);
    }

    public void setQuestPositions(Collection<RelativeQuestPosition> questPositions) {
        this.questPositions = questPositions;
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        midX = getWidth() / 2;
        midY = getHeight() / 2;
    }

    public Quest getQuestAt(int x, int y) {
        double minDistanceSqrt = Integer.MAX_VALUE;
        Quest minDistanceQuest = null;

        for (Map.Entry<Quest, int[]> entry : questRenderedPositions.entrySet()) {
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
        questRenderedPositions.clear();
        for (RelativeQuestPosition questPosition : questPositions) {
            int size = getSize(questPosition.getDistance());
            if (size == 0) {
                continue;
            }

            double angle = toMinusPiPlusPiRange(questPosition.getAngle());
            if (angle < -ANGLE / 2 || angle > ANGLE / 2) {
                continue;
            }

            int drawX = (int) (midX + angle * POST_MULTI);

            questRenderedPositions.put(questPosition.getQuest(), new int[]{drawX, midY});

            canvas.drawBitmap(questionMark, null, new RectF(
                            (int) (drawX - size),
                            (int) (midY - size * 1.4),
                            (int) (drawX + size),
                            (int) (midY + size * 1.4)
                    ),
                    null);

            paint.setTextSize(size);

            String questName = questPosition.getQuest().getName();

            canvas.drawText(questName, drawX - paint.measureText(questName) / 2, (int) (midY + size * 2), paint);
        }
    }

    private int getSize(double distance) {
        if (distance > 100) {
            return 0;
        }

        return 100 - (int) distance;
    }

    private double toMinusPiPlusPiRange(double angle) {
        while (angle > Math.PI) {
            angle -= Math.PI * 2;
        }
        while (angle < -Math.PI) {
            angle += Math.PI * 2;
        }

        return angle;
    }
}
