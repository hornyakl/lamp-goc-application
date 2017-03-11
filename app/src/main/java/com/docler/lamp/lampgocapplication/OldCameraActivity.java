package com.docler.lamp.lampgocapplication;

import android.content.Context;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.docler.lamp.lampgocapplication.utils.Compatibility;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class OldCameraActivity extends AppCompatActivity {
    CameraView cameraView;
    DisplayMetrics displayMetrics;
    Camera camera;
    public int screenWidth;
    public int screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_camera);

        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;

        cameraView = new CameraView(this);
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
            try {
                List<Camera.Size> supportedSizes = null;
                supportedSizes = Compatibility.getSupportedPreviewSizes(parameters);

                Iterator<Camera.Size> itr = supportedSizes.iterator();
                while (itr.hasNext()) {
                    Camera.Size element = itr.next();
                    element.width -= w;
                    element.height -= h;
                }
                Collections.sort(supportedSizes, new ResolutionsOrder());
                parameters.setPreviewSize(w + supportedSizes.get(supportedSizes.size()-1).width, h + supportedSizes.get(supportedSizes.size()-1).height);
            } catch (Exception ex) {
                parameters.setPreviewSize(oldCameraActivity.screenWidth , oldCameraActivity.screenHeight);
            }

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
            camera.setPreviewDisplay(holder);
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

class ResolutionsOrder implements java.util.Comparator<Camera.Size> {
    public int compare(Camera.Size left, Camera.Size right) {

        return Float.compare(left.width + left.height, right.width + right.height);
    }
}