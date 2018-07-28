package com.demskigroup.guitaramps.utility;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.IOException;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * <h>ImageSurfaceView</h>
 * <p>
 *     This class is getting called from CameraActivity class. In this we extend to
 *     SurfaceView class to present a live mCamera preview to the user. This class
 *     implements SurfaceHolder.Callback in order to capture the callback events
 *     for creating and destroying the view, which are needed for assigning the mCamera
 *     preview input.
 * </p>
 * @since 5/1/2017
 */
public class ImageSurfaceView extends SurfaceView implements SurfaceHolder.Callback
{
    private static final String TAG = ImageSurfaceView.class.getSimpleName();
    private Camera camera;
    private SurfaceHolder surfaceHolder;
    private Activity mActivity;
    private String[] permissionsArray;

    public ImageSurfaceView(Activity activity, Camera camera) {
        super(activity);
        this.mActivity=activity;
        this.camera = camera;
        this.surfaceHolder = getHolder();
        this.surfaceHolder.addCallback(this);
        permissionsArray=new String[]{CAMERA,WRITE_EXTERNAL_STORAGE};
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (checkPermissions(permissionsArray))
            {
                this.camera.setPreviewDisplay(holder);
                this.camera.startPreview();
            }
            else ActivityCompat.requestPermissions(mActivity, permissionsArray, 635);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        int cameraId = findFrontFacingCamera();
        if (cameraId < 0) {
            Toast.makeText(mActivity, "No  camera found.", Toast.LENGTH_LONG).show();
        } else {
            if (checkPermissions(permissionsArray)) {
                camera.setDisplayOrientation(90);
                camera.startPreview();
                refreshCamera();
            }
            else ActivityCompat.requestPermissions(mActivity, permissionsArray, 635);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        int cameraId = findFrontFacingCamera();
        if (cameraId < 0) {
            Toast.makeText(mActivity, "No  camera found.", Toast.LENGTH_LONG).show();
        } else {
            if (checkPermissions(permissionsArray)) {
                this.surfaceHolder.removeCallback(this);
                this.camera.stopPreview();
                this.camera.release();
            } else ActivityCompat.requestPermissions(mActivity, permissionsArray, 635);
        }
    }

    public boolean checkPermissions(String[] permissions)
    {
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(mActivity, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void refreshCamera() {
        if (surfaceHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            camera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {

        }
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                Log.d(TAG, "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }
}