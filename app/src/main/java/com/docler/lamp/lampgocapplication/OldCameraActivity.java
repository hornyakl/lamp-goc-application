package com.docler.lamp.lampgocapplication;

import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_camera);

//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);

        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

//        getSupportActionBar().hide();

        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;

        cameraView = new CameraView(this);

        FrameLayout frame = (FrameLayout) findViewById(R.id.camera_image);
        frame.addView(cameraView);

        drawView = new CameraDrawView(this);
        frame.addView(drawView);
    }

//    protected void updateEulerAngles(double x, double y, double z) {
//
//        MatrixF4x4 matrix = new MatrixF4x4();
//        orientationProvider.getRotationMatrix(matrix);
//
//        Vector4f xv = new Vector4f(1, 0, 0, 0);
//        Vector4f yv = new Vector4f(0, 1, 0, 0);
//        Vector4f zv = new Vector4f(0, 0, 1, 0);
//        Vector4f wv = new Vector4f(0, 0, 0, 1);
//
//        matrix.multiplyVector4fByMatrix(xv);
//        matrix.multiplyVector4fByMatrix(yv);
//        matrix.multiplyVector4fByMatrix(zv);
//        matrix.multiplyVector4fByMatrix(wv);
//
//        double[] doubles = new double[9];
////        doubles[0] = (Math.atan2(xv.getX(), xv.getY()));
////        doubles[1] = (Math.atan2(xv.getY(), xv.getZ()));
//        doubles[2] = (Math.atan2(xv.getZ(), xv.getX()));
//
////        doubles[3] = (Math.atan2(yv.getX(), yv.getY()));
////        doubles[4] = (Math.atan2(yv.getY(), yv.getZ()));
////        doubles[5] = (Math.atan2(yv.getZ(), yv.getX()));
////
////        doubles[6] = (Math.atan2(zv.getX(), zv.getY()));
////        doubles[7] = (Math.atan2(zv.getY(), zv.getZ()));
////        doubles[8] = (Math.atan2(zv.getZ(), zv.getX()));
//
//        drawView.invalidate();
//
//        drawView.testAngles = doubles;
//
//    }

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
            drawView.addPoint(quest.getLatitude(), quest.getLongitude());
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
