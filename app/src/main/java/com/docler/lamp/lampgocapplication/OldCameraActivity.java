package com.docler.lamp.lampgocapplication;

import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import com.docler.lamp.lampgocapplication.Quest.Quest;

import java.io.IOException;
import java.util.List;

public class OldCameraActivity extends MovementAwareActivity {
    CameraView cameraView;
    DisplayMetrics displayMetrics;
    Camera camera;
    public int screenWidth;
    public int screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        getSupportActionBar().hide();

        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;

        cameraView = new CameraView(this);
        setContentView(cameraView);
    }

    @Override
    protected void updateEulerAngles(double x, double y, double z) {

    }

    @Override
    protected void updateLocation(double latitude, double longitude) {

    }

    @Override
    protected void onQuests(List<Quest> quests) {

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
