package com.docler.lamp.lampgocapplication;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.docler.lamp.lampgocapplication.Quest.Quest;
import com.docler.lamp.lampgocapplication.sensorFusion.orientationProvider.ImprovedOrientationSensor2Provider;
import com.docler.lamp.lampgocapplication.sensorFusion.representation.MatrixF4x4;
import com.docler.lamp.lampgocapplication.sensorFusion.representation.Vector4f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OldCameraActivity extends MovementAwareActivity {
    CameraView cameraView;
    DisplayMetrics displayMetrics;
    Camera camera;
    public int screenWidth;
    public int screenHeight;

    private CameraDrawView drawView;

    private QuestDoneProvider questDoneProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_camera);

        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        getSupportActionBar().hide();

        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;

        cameraView = new CameraView(this);

        FrameLayout frame = (FrameLayout) findViewById(R.id.camera_image);
        frame.addView(cameraView);

        drawView = new CameraDrawView(this);
        frame.addView(drawView);

        frame.setOnTouchListener(new CameraOnTouchListener());

        questDoneProvider = new QuestDoneProvider();
    }

    protected void updateOrientation(ImprovedOrientationSensor2Provider orientationProvider)
    {
        MatrixF4x4 matrix = new MatrixF4x4();
        orientationProvider.getRotationMatrix(matrix);

        Vector4f xVector = new Vector4f(1, 0, 0, 0);

        matrix.multiplyVector4fByMatrix(xVector);

        double angle = Math.atan2(xVector.getZ(), xVector.getX());

        drawView.setAngle(angle);
    }

    @Override
    protected void updateLocation(double latitude, double longitude) {
        drawView.setLocation(latitude, longitude);
    }

    @Override
    protected void onQuests(List<Quest> quests) {
        drawView.clearPoints();
        for (Quest quest : quests) {
            drawView.addPoint(quest);
        }
    }


    private class CameraOnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() != MotionEvent.ACTION_DOWN)
            {
                return false;
            }

            Quest quest = drawView.getQuestAt((int)event.getX(), (int)event.getY());

            //questDoneProvider.acceptQuest(quest);
            application.currentQuest = quest;

            Intent intent = new Intent(OldCameraActivity.this, AcceptQuest.class);
            startActivity(intent);

            return true;
        }
    }
}

class CameraView extends SurfaceView implements SurfaceHolder.Callback {
    OldCameraActivity oldCameraActivity;
    SurfaceHolder holder;
    Camera camera;

    public CameraView(Context context) {
        super(context);
        oldCameraActivity = (OldCameraActivity) context;

        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        try {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewSize(oldCameraActivity.screenWidth , oldCameraActivity.screenHeight);

            camera.setParameters(parameters);
            camera.startPreview();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (camera != null) {
                try {
                    camera.stopPreview();
                } catch (Exception ignore) {
                }
                try {
                    camera.release();
                } catch (Exception ignore) {
                }
                camera = null;
            }

            camera = Camera.open();
            camera.setDisplayOrientation(90);
            oldCameraActivity.camera = camera;
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException ex) {
            try {
                if (camera != null) {
                    try {
                        camera.stopPreview();
                    } catch (Exception ignore) {
                    }
                    try {
                        camera.release();
                    } catch (Exception ignore) {
                    }
                    camera = null;
                }
            } catch (Exception ignore) {

            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            if (camera != null) {
                try {
                    camera.stopPreview();
                } catch (Exception ignore) {
                }
                try {
                    camera.release();
                } catch (Exception ignore) {
                }
                camera = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
