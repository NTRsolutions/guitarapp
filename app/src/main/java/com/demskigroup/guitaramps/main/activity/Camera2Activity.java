package com.demskigroup.guitaramps.main.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.adapter.ImagesHorizontalRvAdap;
import com.demskigroup.guitaramps.fcm_push_notification.Config;
import com.demskigroup.guitaramps.fcm_push_notification.NotificationMessageDialog;
import com.demskigroup.guitaramps.fcm_push_notification.NotificationUtils;
import com.demskigroup.guitaramps.utility.AutoFitTextureView;
import com.demskigroup.guitaramps.utility.CapturedImage;
import com.demskigroup.guitaramps.utility.CommonClass;
import com.demskigroup.guitaramps.utility.RunTimePermission;
import com.demskigroup.guitaramps.utility.VariableConstants;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * <h>CameraActivity</h>
 * <p>
 *     In this class we used to do the custom camera funtionality on AutoFitTextureView. we
 *     have a custom button to capture image. Once we capture image we save the path of image
 *     into list from File. In this we have maximum 5 picture to take either by camera or gallery.
 * </p>
 * @since 14-Sep-17
 * @author 3Embed
 * @version 1.0
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Camera2Activity extends AppCompatActivity implements View.OnClickListener
{
    private Activity mActivity;
    private String[] permissionsArray;
    private RunTimePermission runTimePermission;
    private ImagesHorizontalRvAdap imagesHorizontalRvAdap;
    private ArrayList<CapturedImage> arrayListImgPath;
    public boolean isToCaptureImage, isFromUpdatePost, isToOpenCamera;
    private HandlerThread mBackgroundThread;
    private boolean isToReleaseCamera;


    /**
     * Xml Variables
     */
    RelativeLayout rL_rootview;
    public TextView tV_upload;

    /**
     * Conversion from screen rotation to JPEG orientation.
     */
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    /**
     * Fop the oneplus and nexux6 issue
     */
    private boolean readyToCapture=true;

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    /**
     * Tag for the {@link Log}.
     */
    private static final String TAG = Camera2Activity.class.getSimpleName();

    /**
     * Camera state: Showing camera preview.
     */
    private static final int STATE_PREVIEW = 0;

    /**
     * Camera state: Waiting for the focus to be locked.
     */
    private static final int STATE_WAITING_LOCK = 1;

    /**
     * Camera state: Waiting for the exposure to be precapture state.
     */
    private static final int STATE_WAITING_PRECAPTURE = 2;

    /**
     * Camera state: Waiting for the exposure state to be something other than precapture.
     */
    private static final int STATE_WAITING_NON_PRECAPTURE = 3;

    /**
     * Camera state: Picture was taken.
     */
    private static final int STATE_PICTURE_TAKEN = 4;

    /**
     * Max preview width that is guaranteed by Camera2 API
     */
    private static final int MAX_PREVIEW_WIDTH = 1920;

    /**
     * Max preview height that is guaranteed by Camera2 API
     */
    private static final int MAX_PREVIEW_HEIGHT = 1080;
    private NotificationMessageDialog mNotificationMessageDialog;

    public static final String CAMERA_FRONT = "1";
    public static final String CAMERA_BACK = "0";

    private String cameraId = CAMERA_BACK;
    private boolean isFlashSupported;
    private boolean isTorchOn = false;
    private ImageView iV_flashIcon;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);

        initVariables();
    }

    private void initVariables() {
        mActivity = Camera2Activity.this;
        isToCaptureImage=true;
        isToReleaseCamera = true;
        mNotificationMessageDialog=new NotificationMessageDialog(mActivity);
        isToOpenCamera = true;

        // receive datas from last activity
        Intent intent=getIntent();
        isFromUpdatePost=intent.getBooleanExtra("isUpdatePost",false);

        // set status bar color
        CommonClass.statusBarColor(mActivity);

        arrayListImgPath = new ArrayList<>();
        mTextureView = (AutoFitTextureView) findViewById(R.id.texture);

        rL_rootview = (RelativeLayout) findViewById(R.id.rL_rootview);
        permissionsArray = new String[]{CAMERA, WRITE_EXTERNAL_STORAGE};
        runTimePermission = new RunTimePermission(mActivity, permissionsArray, true);

        iV_flashIcon = (ImageView) findViewById(R.id.iV_flashIcon);
        iV_flashIcon.setOnClickListener(this);

        // Adapter
        tV_upload= (TextView) findViewById(R.id.tV_upload);
        imagesHorizontalRvAdap=new ImagesHorizontalRvAdap(mActivity,arrayListImgPath,tV_upload,isToCaptureImage);
        RecyclerView rV_cameraImages = (RecyclerView) findViewById(R.id.rV_cameraImages);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(mActivity,LinearLayoutManager.HORIZONTAL,false);
        rV_cameraImages.setLayoutManager(linearLayoutManager);
        rV_cameraImages.setAdapter(imagesHorizontalRvAdap);

        // Done
        RelativeLayout rL_done = (RelativeLayout)findViewById(R.id.rL_done);
        rL_done.setOnClickListener(this);

        // gallery
        ImageView iV_gallery= (ImageView) findViewById(R.id.iV_gallery);
        iV_gallery.setOnClickListener(this);

        // Capture image
        ImageView iV_captureImage = (ImageView)findViewById(R.id.iV_captureImage);
        iV_captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // check captured images are less than 5 or not
                if (arrayListImgPath.size()<5)
                {
                    System.out.println(TAG+" "+"isToCaptureImage="+isToCaptureImage);
                    //take the picture
                    if (isToCaptureImage)
                    {
                        lockFocus();
                        //isToCaptureImage=false;
                    }
                }
                else CommonClass.showSnackbarMessage(rL_rootview,getResources().getString(R.string.you_can_select_only_upto));
            }
        });

        // Cancel
        RelativeLayout rL_cancel_btn= (RelativeLayout) findViewById(R.id.rL_cancel_btn);
        rL_cancel_btn.setOnClickListener(this);
    }

    /**
     * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a
     * {@link TextureView}.
     */
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height)
        {
            System.out.println(TAG+" "+"openCamera from onSurfaceTextureAvailable");
            if (isToOpenCamera)
                openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        }
    };

    /**
     * ID of the current {@link CameraDevice}.
     */
    private String mCameraId;

    /**
     * An {@link AutoFitTextureView} for camera preview.
     */
    private AutoFitTextureView mTextureView;

    /**
     * A {@link CameraCaptureSession } for camera preview.
     */
    private CameraCaptureSession mCaptureSession;

    /**
     * A reference to the opened {@link CameraDevice}.
     */
    private CameraDevice mCameraDevice;

    /**
     * The {@link android.util.Size} of camera preview.
     */
    private Size mPreviewSize;

    /**
     * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its state.
     */
    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            mCameraOpenCloseLock.release();
            mCameraDevice = cameraDevice;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }
    };

    /**
     * A {@link Handler} for running tasks in the background.
     */
    private Handler mBackgroundHandler;

    /**
     * An {@link ImageReader} that handles still image capture.
     */
    private ImageReader mImageReader;

    /**
     * This is the output file for our picture.
     */
    private File mFile=null;

    /**
     * This a callback object for the {@link ImageReader}. "onImageAvailable" will be called when a
     * still image is ready to be saved.
     */
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            //mFile = new File(getActivity().getExternalFilesDir(null), String.valueOf(System.nanoTime())+"pic.jpg");

            System.out.println(TAG+" "+"onImageAvailable...");

            String folderPath;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                folderPath = Environment.getExternalStorageDirectory() + "/" + getResources().getString(R.string.app_name);
            } else {

                folderPath = mActivity.getFilesDir() + "/" + getResources().getString(R.string.app_name);
            }

            File folder = new File(folderPath);

            if (!folder.exists() && !folder.isDirectory()) {


                folder.mkdirs();
            }

            if(mFile==null) {

                mFile = new File(folderPath, String.valueOf(System.currentTimeMillis()) + ".jpg");
            }
            try {

                if (!mFile.exists()) {

                    mFile.createNewFile();
                }
            } catch (Exception e) {
                Toast.makeText(mActivity, "Image Not saved", Toast.LENGTH_SHORT).show();
                return;
            }

            mBackgroundHandler.post(new ImageSaver(reader.acquireNextImage(), mFile));

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mFile!=null) {
                        CapturedImage image = new CapturedImage();
                        image.setImagePath(mFile.getAbsolutePath());
                        image.setRotateAngle(0);
                        arrayListImgPath.add(image);

                        imagesHorizontalRvAdap.notifyItemInserted(arrayListImgPath.size());
                        //imagesHorizontalRvAdap.notifyDataSetChanged();

                        isToCaptureImage = arrayListImgPath.size() < 5;

                        // enable upload button
                        if (arrayListImgPath.size() > 0)
                            tV_upload.setTextColor(ContextCompat.getColor(mActivity, R.color.colorAccent));

                        mFile=null;
                        readyToCapture=true;
                    }
                }
            });
        }
    };


    /**
     * {@link CaptureRequest.Builder} for the camera preview
     */
    private CaptureRequest.Builder mPreviewRequestBuilder;

    /**
     * {@link CaptureRequest} generated by {@link #mPreviewRequestBuilder}
     */
    private CaptureRequest mPreviewRequest;

    /**
     * The current state of camera state for taking pictures.
     *
     * @see #mCaptureCallback
     */
    private int mState = STATE_PREVIEW;

    /**
     * A {@link Semaphore} to prevent the app from exiting before closing the camera.
     */
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    /**
     * Whether the current camera device supports Flash or not.
     */
    private boolean mFlashSupported;

    /**
     * Orientation of the camera sensor
     */
    private int mSensorOrientation;

    /**
     * A {@link CameraCaptureSession.CaptureCallback} that handles events related to JPEG capture.
     */
    private CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        private void process(CaptureResult result) {
            switch (mState) {
                case STATE_PREVIEW: {
                    // We have nothing to do when the camera preview is working normally.
                    break;
                }
                case STATE_WAITING_LOCK: {
                    Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);

                    Log.d("log11","17");

                    if (afState == null) {
                        Log.d("log11","18");

                        if(readyToCapture){
                            readyToCapture=false;
                            captureStillPicture();
                        }
                    } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                            CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                        Log.d("log11","19");
                        // CONTROL_AE_STATE can be null on some devices
                        Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                        if (aeState == null ||
                                aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                            if(readyToCapture){

                                readyToCapture=false;
                                Log.d("log11","20");
                                mState = STATE_PICTURE_TAKEN;
                                captureStillPicture();}
                        } else {

                            Log.d("log11","21");
                            runPrecaptureSequence();
                        }
                    }
                    break;
                }
                case STATE_WAITING_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null ||
                            aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                            aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        mState = STATE_WAITING_NON_PRECAPTURE;
                    }
                    break;
                }
                case STATE_WAITING_NON_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        mState = STATE_PICTURE_TAKEN;


                        if(readyToCapture){

                            readyToCapture=false;
                            captureStillPicture();
                        }
                    }
                    break;
                }
            }
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session,
                                        @NonNull CaptureRequest request,
                                        @NonNull CaptureResult partialResult) {
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                       @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            process(result);
        }
    };

    /**
     * Given {@code choices} of {@code Size}s supported by a camera, choose the smallest one that
     * is at least as large as the respective texture view size, and that is at most as large as the
     * respective max size, and whose aspect ratio matches with the specified value. If such size
     * doesn't exist, choose the largest one that is at most as large as the respective max size,
     * and whose aspect ratio matches with the specified value.
     *
     * @param choices           The list of sizes that the camera supports for the intended output
     *                          class
     * @param textureViewWidth  The width of the texture view relative to sensor coordinate
     * @param textureViewHeight The height of the texture view relative to sensor coordinate
     * @param maxWidth          The maximum width that can be chosen
     * @param maxHeight         The maximum height that can be chosen
     * @param aspectRatio       The aspect ratio
     * @return The optimal {@code Size}, or an arbitrary one if none were big enough
     */
    private Size chooseOptimalSize(Size[] choices, int textureViewWidth,
                                   int textureViewHeight, int maxWidth, int maxHeight, Size aspectRatio) {

        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<Size> notBigEnough = new ArrayList<>();


        Log.d("log73",maxHeight+" "+maxWidth);

        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            Log.d("log92",option.getHeight()+" "+option.getWidth());

            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight &&
                    option.getHeight() == option.getWidth() * h / w) {
                if (option.getWidth() >= textureViewWidth &&
                        option.getHeight() >= textureViewHeight) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
            }
        }

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else if (notBigEnough.size() > 0) {
            return Collections.max(notBigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startBackgroundThread();

        // When the screen is turned off and turned back on, the SurfaceTexture is already
        // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
        // a camera and start preview from here (otherwise, we wait until the surface is ready in
        // the SurfaceTextureListener).
        if (mTextureView.isAvailable() && isToOpenCamera) {
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mNotificationMessageDialog.mRegistrationBroadcastReceiver, new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mNotificationMessageDialog.mRegistrationBroadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }


    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mNotificationMessageDialog.mRegistrationBroadcastReceiver);
        closeCamera();
        stopBackgroundThread();
        System.out.println(TAG+" "+"on pause called..");
        super.onPause();
    }

    private void closeCamera()
    {
        try
        {
            mCameraOpenCloseLock.acquire();
            if (null != mCaptureSession)
            {
                mCaptureSession.close();
                mCaptureSession = null;
            }
            if (null != mCameraDevice)
            {
                mCameraDevice.close();
                mCameraDevice = null;
            }

            if (null != mImageReader)
            {
                mImageReader.close();
                mImageReader = null;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            mCameraOpenCloseLock.release();
        }
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private void stopBackgroundThread()
    {
        if(mBackgroundThread!=null)
        {
            mBackgroundThread.quitSafely();
        }
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Sets up member variables related to camera.
     *
     * @param width  The width of available size for camera preview
     * @param height The height of available size for camera preview
     */
    private void setUpCameraOutputs(int width, int height) {


        Log.d("log123","123"+width+" "+height);
        CameraManager manager = (CameraManager) mActivity.getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics
                        = manager.getCameraCharacteristics(cameraId);

                // We don't use a front facing camera in this sample.
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }

                StreamConfigurationMap map = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) {
                    continue;
                }

                // For still image captures, we use the largest available size.
                Size largest = Collections.max(
                        Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new CompareSizesByArea());
                mImageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(),
                        ImageFormat.JPEG, /*maxImages*/2);
                mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mBackgroundHandler);

                // Find out if we need to swap dimension to get the preview size relative to sensor
                // coordinate.
                int displayRotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
                //noinspection ConstantConditions
                mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                boolean swappedDimensions = false;
                switch (displayRotation) {
                    case Surface.ROTATION_0:
                    case Surface.ROTATION_180:
                        if (mSensorOrientation == 90 || mSensorOrientation == 270) {
                            swappedDimensions = true;
                        }
                        break;
                    case Surface.ROTATION_90:
                    case Surface.ROTATION_270:
                        if (mSensorOrientation == 0 || mSensorOrientation == 180) {
                            swappedDimensions = true;
                        }
                        break;
                    default:
                        Log.e(TAG, "Display rotation is invalid: " + displayRotation);
                }

                Point displaySize = new Point();
                mActivity.getWindowManager().getDefaultDisplay().getSize(displaySize);
                int rotatedPreviewWidth = width;
                int rotatedPreviewHeight = height;
                int maxPreviewWidth = displaySize.x;
                int maxPreviewHeight = displaySize.y;
                Log.d("log13",maxPreviewWidth+" "+maxPreviewHeight);

                if (swappedDimensions) {
                    rotatedPreviewWidth = height;
                    rotatedPreviewHeight = width;
                    maxPreviewWidth = displaySize.y;
                    maxPreviewHeight = displaySize.x;
                }

                // Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
                // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
                // garbage capture data.
                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                        rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth,
                        maxPreviewHeight, largest);

                // We fit the aspect ratio of TextureView to the size of preview we picked.
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    Log.d("log17",mPreviewSize.getWidth()+" "+ mPreviewSize.getHeight());
                    mTextureView.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                } else {
                    Log.d("log18",mPreviewSize.getWidth()+" "+ mPreviewSize.getHeight());
                    mTextureView.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
                }

                // Check if the flash is supported.
                Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                mFlashSupported = available == null ? false : available;

                mCameraId = cameraId;
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
            CommonClass.showSnackbarMessage(rL_rootview, getString(R.string.camera_error));
        }
    }

    /**
     * Opens the camera specified by {@link Camera2Activity#mCameraId}.
     */
    private void openCamera(int width, int height) {
        System.out.println(TAG+" "+"openCamera called...");
        if (runTimePermission.checkPermissions(permissionsArray)) {
            setUpCameraOutputs(width, height);
            configureTransform(width, height);
//   configureTransform(reqWidth, reqHeight);
            CameraManager manager = (CameraManager) mActivity.getSystemService(Context.CAMERA_SERVICE);
            try {
                if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                    throw new RuntimeException("Time out waiting to lock camera opening.");
                }
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                manager.openCamera(mCameraId, mStateCallback, mBackgroundHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
            }
        }
        else
        {
            runTimePermission.requestPermission();
            isToOpenCamera=false;
        }
    }

    /**
     * Starts a background thread and its {@link Handler}.
     */
    private void startBackgroundThread() {
        /*
      An additional thread for running tasks that shouldn't block the UI.
     */
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    /**
     * Creates a new {@link CameraCaptureSession} for camera preview.
     */
    private void createCameraPreviewSession() {
        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;

            // We configure the size of default buffer to be the size of camera preview we want.
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

            // This is the output Surface we need to start preview.
            Surface surface = new Surface(texture);

            // We set up a CaptureRequest.Builder with the output Surface.
            mPreviewRequestBuilder
                    = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);

            // Here, we create a CameraCaptureSession for camera preview.
            mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            // The camera is already closed
                            if (null == mCameraDevice) {
                                return;
                            }
                            Log.d("log81","85");
                            // When the session is ready, we start displaying the preview.
                            mCaptureSession = cameraCaptureSession;

                            Log.d("log81","88");
                            // Auto focus should be continuous for camera preview.
                            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

                            Log.d("log81","87");
                            // Flash is automatically enabled when necessary.
                            //setAutoFlash(mPreviewRequestBuilder);
                            Log.d("log81","86");
                            // Finally, we start displaying the camera preview.
                            mPreviewRequest = mPreviewRequestBuilder.build();

                            Log.d("log81","84");
                            /**
                             * Ashutosh
                             */
                            try {


                                Log.d("log81","81");

                                mCaptureSession.setRepeatingRequest(mPreviewRequest,
                                        mCaptureCallback, mBackgroundHandler);
                            } catch (CameraAccessException e) {


                                /**
                                 * cameraaccessexception
                                 */
                                e.printStackTrace();
                            }
                            finally {
                                if (mCameraOpenCloseLock!=null)
                                    mCameraOpenCloseLock.release();

                                /*if (mCameraDevice!=null)
                                    mCameraDevice.close();*/
                            }
                        }

                        @Override
                        public void onConfigureFailed(
                                @NonNull CameraCaptureSession cameraCaptureSession) {
                            System.out.println(TAG+" "+"failed");
                        }
                    }, null
            );
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Configures the necessary {@link android.graphics.Matrix} transformation to `mTextureView`.
     * This method should be called after the camera preview size is determined in
     * setUpCameraOutputs and also the size of `mTextureView` is fixed.
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */
    private void configureTransform(int viewWidth, int viewHeight) {
        if (null == mTextureView || null == mPreviewSize || null == mActivity) {
            return;
        }
        int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());

        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        mTextureView.setTransform(matrix);
        Log.d("log12999",mPreviewSize.getHeight()+" "+mPreviewSize.getWidth()+" "+viewHeight+" "+viewWidth);
    }

    /**
     * Lock the focus as the first step for a still image capture.
     */
    private void lockFocus() {
        try {
            // This is how to tell the camera to lock focus.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the lock.
            mState = STATE_WAITING_LOCK;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        finally {
            if (mCameraOpenCloseLock!=null)
                mCameraOpenCloseLock.release();
        }
    }

    /**
     * Run the precapture sequence for capturing a still image. This method should be called when
     * we get a response in {@link #mCaptureCallback} from {@link #lockFocus()}.
     */
    private void runPrecaptureSequence() {
        try {
            // This is how to tell the camera to trigger.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the precapture sequence to be set.
            mState = STATE_WAITING_PRECAPTURE;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        finally {
            if (mCameraOpenCloseLock!=null)
                mCameraOpenCloseLock.release();
        }
    }

    /**
     * Capture a still picture. This method should be called when we get a response in
     * {@link #mCaptureCallback} from both {@link #lockFocus()}.
     */
    private void captureStillPicture() {
        try {
            if (null == mActivity || null == mCameraDevice)
            {
                return;
            }
            // This is the CaptureRequest.Builder that we use to take a picture.
            final CaptureRequest.Builder captureBuilder =
                    mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReader.getSurface());
            // Use the same AE and AF modes as the preview.
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            //setAutoFlash(captureBuilder);
            // Orientation
            int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation(rotation));
            CameraCaptureSession.CaptureCallback CaptureCallback = new CameraCaptureSession.CaptureCallback()
            {

                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                               @NonNull CaptureRequest request,
                                               @NonNull TotalCaptureResult result) {
                    unlockFocus();
                }
            };

            mCaptureSession.stopRepeating();
            mCaptureSession.capture(captureBuilder.build(), CaptureCallback, null);
        } catch (CameraAccessException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the JPEG orientation from the specified screen rotation.
     *
     * @param rotation The screen rotation.
     * @return The JPEG orientation (one of 0, 90, 270, and 360)
     */
    private int getOrientation(int rotation) {
        // Sensor orientation is 90 for most devices, or 270 for some devices (eg. Nexus 5X)
        // We have to take that into account and rotate JPEG properly.
        // For devices with orientation of 90, we simply return our mapping from ORIENTATIONS.
        // For devices with orientation of 270, we need to rotate the JPEG 180 degrees.
        return (ORIENTATIONS.get(rotation) + mSensorOrientation + 270) % 360;
    }

    /**
     * Unlock the focus. This method should be called when still image capture sequence is
     * finished.
     */
    private void unlockFocus() {
        try {
            // Reset the auto-focus trigger
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            //setAutoFlash(mPreviewRequestBuilder);
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
            // After this, the camera will go back to the normal state of preview.
            mState = STATE_PREVIEW;

            Log.d("log81","82");
            mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        finally {
            if (mCameraOpenCloseLock!=null)
                mCameraOpenCloseLock.release();
        }

    }

    private void setAutoFlash(CaptureRequest.Builder requestBuilder) {
        if (mFlashSupported) {
            requestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
        }
    }

    /**
     * Saves a JPEG {@link Image} into the specified {@link File}.
     */
    private class ImageSaver implements Runnable
    {
        /**
         * The JPEG image
         */
        private final Image mImage;
        /**
         * The file we save the image into.
         */
        private final File mFile;

        ImageSaver(Image image, File file) {
            mImage = image;
            mFile = file;
        }

        @Override
        public void run() {
            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            //saveCapturedImage(bytes);

            buffer.get(bytes);
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(mFile);
                output.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mImage.close();
                if (null != output) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Compares two {@code Size}s based on their areas.
     */
    private class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            // back button
            case R.id.rL_cancel_btn:
                onBackPressed();
                break;

            // gallery
            case R.id.iV_gallery:{
                System.out.println(TAG+" "+"captured image size="+arrayListImgPath.size());

                intent = new Intent(mActivity, GalleryActivity.class);
                ArrayList<String> galleryImagePaths = new ArrayList<>();
                ArrayList<Integer> rotationAngles=new ArrayList<>();

                for (int i = 0; i < arrayListImgPath.size(); i++) {
                    galleryImagePaths.add(arrayListImgPath.get(i).getImagePath());
                    rotationAngles.add(arrayListImgPath.get(i).getRotateAngle());
                }

                intent.putExtra("arrayListImgPath", galleryImagePaths);
                intent.putExtra("rotationAngles",rotationAngles);
                startActivityForResult(intent, VariableConstants.SELECT_GALLERY_IMG_REQ_CODE);

                break;
            }

            // done
            case R.id.rL_done :
                ArrayList<String> galleryImagePaths=new ArrayList<>();
                ArrayList<Integer> rotationAngles=new ArrayList<>();

                for(int i=0;i<arrayListImgPath.size();i++)
                {
                    galleryImagePaths.add(arrayListImgPath.get(i).getImagePath());
                    rotationAngles.add(arrayListImgPath.get(i).getRotateAngle());
                }

                if (isFromUpdatePost)
                {
                    intent =new Intent();
                    intent.putExtra("arrayListImgPath",galleryImagePaths);
                    intent.putExtra("rotationAngles",rotationAngles);
                    setResult(VariableConstants.UPDATE_IMAGE_REQ_CODE,intent);
                    onBackPressed();
                }
                else
                {
                    if (arrayListImgPath.size()>0)
                    {
                        intent=new Intent(mActivity,PostProductActivity.class);
                        intent.putExtra("arrayListImgPath",galleryImagePaths);
                        intent.putExtra("rotationAngles",rotationAngles);
                        startActivity(intent);

                    }
                }
                break;

            // flash light
            case R.id.iV_flashIcon :
                switchFlash();
                /*if (isFlashOn) {
                    // turn off flash
                    turnOffFlash();
                } else {
                    // turn on flash
                    turnOnFlash();
                }*/
                break;
        }
    }

    public void switchFlash() {
        if (cameraId.equals(CAMERA_BACK)) {
            if (isTorchOn) {
                mPreviewRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
                //mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), null, null);
                iV_flashIcon.setImageResource(R.drawable.flash_light_off);
                isTorchOn = false;
            } else {
                mPreviewRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
                //mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), null, null);
                iV_flashIcon.setImageResource(R.drawable.flash_light_on);
                isTorchOn = true;
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case VariableConstants.PERMISSION_REQUEST_CODE :
                System.out.println("grant result="+grantResults.length);
                if (grantResults.length>0)
                {
                    for (int count=0;count<grantResults.length;count++)
                    {
                        if (grantResults[count]!= PackageManager.PERMISSION_GRANTED)
                            runTimePermission.allowPermissionAlert(permissions[count]);

                    }
                    System.out.println("isAllPermissionGranted="+runTimePermission.checkPermissions(permissionsArray));
                    if (runTimePermission.checkPermissions(permissionsArray))
                    {
                        isToOpenCamera = true;
                        System.out.println(TAG+" "+"openCamera from onRequestPermissionsResult");

                        startBackgroundThread();

                        // When the screen is turned off and turned back on, the SurfaceTexture is already
                        // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
                        // a camera and start preview from here (otherwise, we wait until the surface is ready in
                        // the SurfaceTextureListener).
                        if (mTextureView.isAvailable() && isToOpenCamera) {
                            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
                        } else {
                            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
                        }
                        //openCamera(mTextureView.getWidth(),mTextureView.getHeight());
                    }
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(TAG+" "+"on activity result called..."+requestCode);
        if (data != null)
        {
            switch (requestCode)
            {
                case VariableConstants.SELECT_GALLERY_IMG_REQ_CODE :
                    if (data.getStringArrayListExtra("arrayListImgPath")!=null && data.getStringArrayListExtra("arrayListImgPath").size()>0)
                    {
                        tV_upload.setTextColor(ContextCompat.getColor(mActivity,R.color.pink_color));
                        arrayListImgPath.clear();

                        ArrayList<String > imagePathsFromGallery=data.getStringArrayListExtra("arrayListImgPath");
                        CapturedImage image;


                        for(int i=0;i<imagePathsFromGallery.size();i++) {
                            image = new CapturedImage();

                            image.setRotateAngle(0);
                            image.setImagePath(imagePathsFromGallery.get(i));
                            arrayListImgPath.add(image);
                        }
//                        arrayListImgPath.addAll(data.getStringArrayListExtra("arrayListImgPath"));
                        imagesHorizontalRvAdap.notifyDataSetChanged();
                    }
                    else  tV_upload.setTextColor(ContextCompat.getColor(mActivity,R.color.reset_button_bg));
                    break;
            }
        }
    }
}

/*
package com.yelo.com.main.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.yelo.com.R;
import com.yelo.com.adapter.ImagesHorizontalRvAdap;
import com.yelo.com.fcm_push_notification.Config;
import com.yelo.com.fcm_push_notification.NotificationMessageDialog;
import com.yelo.com.fcm_push_notification.NotificationUtils;
import com.yelo.com.utility.AutoFitTextureView;
import com.yelo.com.utility.CapturedImage;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.RunTimePermission;
import com.yelo.com.utility.VariableConstants;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

*/
/**
 * <h>CameraActivity</h>
 * <p>
 *     In this class we used to do the custom camera funtionality on AutoFitTextureView. we
 *     have a custom button to capture image. Once we capture image we save the path of image
 *     into list from File. In this we have maximum 5 picture to take either by camera or gallery.
 * </p>
 * @since 14-Sep-17
 * @author 3Embed
 * @version 1.0
 *//*

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Camera2Activity extends AppCompatActivity implements View.OnClickListener
{
    private Activity mActivity;
    private String[] permissionsArray;
    private RunTimePermission runTimePermission;
    private ImagesHorizontalRvAdap imagesHorizontalRvAdap;
    private ArrayList<CapturedImage> arrayListImgPath;
    public boolean isToCaptureImage, isFromUpdatePost, isToOpenCamera;

    */
/**
 * Xml Variables
 *//*

    RelativeLayout rL_rootview;
    public TextView tV_upload;

    */
/**
 * Conversion from screen rotation to JPEG orientation.
 *//*

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    */
/**
 * Fop the oneplus and nexux6 issue
 *//*

    private boolean readyToCapture=true;

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    */
/**
 * Tag for the {@link Log}.
 *//*

    private static final String TAG = Camera2Activity.class.getSimpleName();

    */
/**
 * Camera state: Showing camera preview.
 *//*

    private static final int STATE_PREVIEW = 0;

    */
/**
 * Camera state: Waiting for the focus to be locked.
 *//*

    private static final int STATE_WAITING_LOCK = 1;

    */
/**
 * Camera state: Waiting for the exposure to be precapture state.
 *//*

    private static final int STATE_WAITING_PRECAPTURE = 2;

    */
/**
 * Camera state: Waiting for the exposure state to be something other than precapture.
 *//*

    private static final int STATE_WAITING_NON_PRECAPTURE = 3;

    */
/**
 * Camera state: Picture was taken.
 *//*

    private static final int STATE_PICTURE_TAKEN = 4;
    private NotificationMessageDialog mNotificationMessageDialog;
    private HandlerThread mBackgroundThread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);

        initVariables();
    }

    private void initVariables() {
        mActivity = Camera2Activity.this;
        isToCaptureImage=true;
        mNotificationMessageDialog=new NotificationMessageDialog(mActivity);
        isToOpenCamera = true;

        // receive datas from last activity
        Intent intent=getIntent();
        isFromUpdatePost=intent.getBooleanExtra("isUpdatePost",false);

        // set status bar color
        CommonClass.statusBarColor(mActivity);

        arrayListImgPath = new ArrayList<>();
        mTextureView = (AutoFitTextureView) findViewById(R.id.texture);

        rL_rootview = (RelativeLayout) findViewById(R.id.rL_rootview);
        permissionsArray = new String[]{CAMERA, WRITE_EXTERNAL_STORAGE};
        runTimePermission = new RunTimePermission(mActivity, permissionsArray, true);

        // Adapter
        tV_upload= (TextView) findViewById(R.id.tV_upload);
        imagesHorizontalRvAdap=new ImagesHorizontalRvAdap(mActivity,arrayListImgPath,tV_upload,isToCaptureImage);
        RecyclerView rV_cameraImages = (RecyclerView) findViewById(R.id.rV_cameraImages);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(mActivity,LinearLayoutManager.HORIZONTAL,false);
        rV_cameraImages.setLayoutManager(linearLayoutManager);
        rV_cameraImages.setAdapter(imagesHorizontalRvAdap);

        // Done
        RelativeLayout rL_done = (RelativeLayout)findViewById(R.id.rL_done);
        rL_done.setOnClickListener(this);

        // gallery
        ImageView iV_gallery= (ImageView) findViewById(R.id.iV_gallery);
        iV_gallery.setOnClickListener(this);

        // Capture image
        ImageView iV_captureImage = (ImageView)findViewById(R.id.iV_captureImage);
        iV_captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // check captured images are less than 5 or not
                if (arrayListImgPath.size()<5)
                {
                    System.out.println(TAG+" "+"isToCaptureImage="+isToCaptureImage);
                    //take the picture
                    if (isToCaptureImage)
                    {
                        lockFocus();
                        isToCaptureImage=false;
                    }
                }
                else CommonClass.showSnackbarMessage(rL_rootview,getResources().getString(R.string.you_can_select_only_upto));
            }
        });

        // Cancel
        RelativeLayout rL_cancel_btn= (RelativeLayout) findViewById(R.id.rL_cancel_btn);
        rL_cancel_btn.setOnClickListener(this);
    }

    */
/**
 * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a
 * {@link TextureView}.
 *//*

    private final TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height)
        {
            System.out.println(TAG+" "+"openCamera from onSurfaceTextureAvailable");
            if (isToOpenCamera)
            openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        }
    };

    */
/**
 * ID of the current {@link CameraDevice}.
 *//*

    private String mCameraId;

    */
/**
 * An {@link AutoFitTextureView} for camera preview.
 *//*

    private AutoFitTextureView mTextureView;

    */
/**
 * A {@link CameraCaptureSession } for camera preview.
 *//*

    private CameraCaptureSession mCaptureSession;

    */
/**
 * A reference to the opened {@link CameraDevice}.
 *//*

    private CameraDevice mCameraDevice;

    */
/**
 * The {@link android.util.Size} of camera preview.
 *//*

    private Size mPreviewSize;

    */
/**
 * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its state.
 *//*

    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback()
    {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            mCameraOpenCloseLock.release();
            mCameraDevice = cameraDevice;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }



        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }
    };

    */
/**
 * A {@link Handler} for running tasks in the background.
 *//*

    private Handler mBackgroundHandler;

    */
/**
 * An {@link ImageReader} that handles still image capture.
 *//*

    private ImageReader mImageReader;

    */
/**
 * This is the output file for our picture.
 *//*

    private File mFile=null;

    */
/**
 * This a callback object for the {@link ImageReader}. "onImageAvailable" will be called when a
 * still image is ready to be saved.
 *//*

    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            //mFile = new File(getActivity().getExternalFilesDir(null), String.valueOf(System.nanoTime())+"pic.jpg");

            System.out.println(TAG+" "+"onImageAvailable...");

            String folderPath;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                folderPath = Environment.getExternalStorageDirectory() + "/" + getResources().getString(R.string.app_name);
            } else {

                folderPath = mActivity.getFilesDir() + "/" + getResources().getString(R.string.app_name);
            }

            File folder = new File(folderPath);

            if (!folder.exists() && !folder.isDirectory()) {


                folder.mkdirs();
            }

            if(mFile==null) {

                mFile = new File(folderPath, String.valueOf(System.currentTimeMillis()) + ".jpg");
            }
            try {

                if (!mFile.exists()) {

                    mFile.createNewFile();
                }
            } catch (Exception e) {
                Toast.makeText(mActivity, "Image Not saved", Toast.LENGTH_SHORT).show();
                return;
            }

            mBackgroundHandler.post(new ImageSaver(reader.acquireNextImage(), mFile));

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mFile!=null) {
                        CapturedImage image = new CapturedImage();
                        image.setImagePath(mFile.getAbsolutePath());
                        image.setRotateAngle(0);
                        arrayListImgPath.add(image);

                        //imagesHorizontalRvAdap.notifyItemInserted(arrayListImgPath.size() - 1);
                        imagesHorizontalRvAdap.notifyDataSetChanged();

                        isToCaptureImage = arrayListImgPath.size() < 5;

                        // enable upload button
                        if (arrayListImgPath.size() > 0)
                            tV_upload.setTextColor(ContextCompat.getColor(mActivity, R.color.pink_color));

                        mFile=null;
                        readyToCapture=true;
                    }
                }
            });
        }
    };


    */
/**
 * {@link CaptureRequest.Builder} for the camera preview
 *//*

    private CaptureRequest.Builder mPreviewRequestBuilder;

    */
/**
 * {@link CaptureRequest} generated by {@link #mPreviewRequestBuilder}
 *//*

    private CaptureRequest mPreviewRequest;

    */
/**
 * The current state of camera state for taking pictures.
 *
 * @see #mCaptureCallback
 *//*

    private int mState = STATE_PREVIEW;

    */
/**
 * A {@link Semaphore} to prevent the app from exiting before closing the camera.
 *//*

    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    */
/**
 * Whether the current camera device supports Flash or not.
 *//*

    private boolean mFlashSupported;

    */
/**
 * Orientation of the camera sensor
 *//*

    private int mSensorOrientation;

    */
/**
 * A {@link CameraCaptureSession.CaptureCallback} that handles events related to JPEG capture.
 *//*

    private CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        private void process(CaptureResult result) {
            switch (mState) {
                case STATE_PREVIEW: {
                    // We have nothing to do when the camera preview is working normally.
                    break;
                }
                case STATE_WAITING_LOCK: {
                    Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);

                    Log.d("log11","17");

                    if (afState == null) {
                        Log.d("log11","18");

                        if(readyToCapture){
                            readyToCapture=false;
                            captureStillPicture();
                        }
                    } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                            CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                        Log.d("log11","19");
                        // CONTROL_AE_STATE can be null on some devices
                        Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                        if (aeState == null ||
                                aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                            if(readyToCapture){

                                readyToCapture=false;
                                Log.d("log11","20");
                                mState = STATE_PICTURE_TAKEN;
                                captureStillPicture();}
                        } else {

                            Log.d("log11","21");
                            runPrecaptureSequence();
                        }
                    }
                    break;
                }
                case STATE_WAITING_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null ||
                            aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                            aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        mState = STATE_WAITING_NON_PRECAPTURE;
                    }
                    break;
                }
                case STATE_WAITING_NON_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        mState = STATE_PICTURE_TAKEN;


                        if(readyToCapture){

                            readyToCapture=false;
                            captureStillPicture();}
                    }
                    break;
                }
            }
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session,
                                        @NonNull CaptureRequest request,
                                        @NonNull CaptureResult partialResult) {
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                       @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            process(result);
        }
    };

    */
/**
 * Given {@code choices} of {@code Size}s supported by a camera, choose the smallest one that
 * is at least as large as the respective texture view size, and that is at most as large as the
 * respective max size, and whose aspect ratio matches with the specified value. If such size
 * doesn't exist, choose the largest one that is at most as large as the respective max size,
 * and whose aspect ratio matches with the specified value.
 *
 * @param choices           The list of sizes that the camera supports for the intended output
 *                          class
 * @param textureViewWidth  The width of the texture view relative to sensor coordinate
 * @param textureViewHeight The height of the texture view relative to sensor coordinate
 * @param maxWidth          The maximum width that can be chosen
 * @param maxHeight         The maximum height that can be chosen
 * @param aspectRatio       The aspect ratio
 * @return The optimal {@code Size}, or an arbitrary one if none were big enough
 *//*

    private Size chooseOptimalSize(Size[] choices, int textureViewWidth,
                                   int textureViewHeight, int maxWidth, int maxHeight, Size aspectRatio) {

        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<Size> notBigEnough = new ArrayList<>();


        Log.d("log73",maxHeight+" "+maxWidth);

        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            Log.d("log92",option.getHeight()+" "+option.getWidth());

            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight &&
                    option.getHeight() == option.getWidth() * h / w) {
                if (option.getWidth() >= textureViewWidth &&
                        option.getHeight() >= textureViewHeight) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
            }
        }

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else if (notBigEnough.size() > 0) {
            return Collections.max(notBigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startBackgroundThread();

        // When the screen is turned off and turned back on, the SurfaceTexture is already
        // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
        // a camera and start preview from here (otherwise, we wait until the surface is ready in
        // the SurfaceTextureListener).
        if (mTextureView.isAvailable() && isToOpenCamera) {
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mNotificationMessageDialog.mRegistrationBroadcastReceiver, new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mNotificationMessageDialog.mRegistrationBroadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mNotificationMessageDialog.mRegistrationBroadcastReceiver);
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    */
/**
 * Closes the current {@link CameraDevice}.
 *//*

    private void closeCamera()
    {
        try
        {
            mCameraOpenCloseLock.acquire();
            if (null != mCaptureSession)
            {
                mCaptureSession.close();
                mCaptureSession = null;
            }
            if (null != mCameraDevice)
            {
                mCameraDevice.close();
                mCameraDevice = null;
            }

            if (null != mImageReader)
            {
                mImageReader.close();
                mImageReader = null;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            mCameraOpenCloseLock.release();
        }
    }

    */
/**
 * Stops the background thread and its {@link Handler}.
 *//*

    private void stopBackgroundThread()
    {
        if(mBackgroundThread!=null)
        {
            mBackgroundThread.quitSafely();
        }
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    */
/**
 * Sets up member variables related to camera.
 *
 * @param width  The width of available size for camera preview
 * @param height The height of available size for camera preview
 *//*

    private void setUpCameraOutputs(int width, int height) {


        Log.d("log123","123"+width+" "+height);
        CameraManager manager = (CameraManager) mActivity.getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics
                        = manager.getCameraCharacteristics(cameraId);

                // We don't use a front facing camera in this sample.
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }

                StreamConfigurationMap map = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) {
                    continue;
                }

                // For still image captures, we use the largest available size.
                Size largest = Collections.max(
                        Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new CompareSizesByArea());
                mImageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(),
                        ImageFormat.JPEG, */
/*maxImages*//*
2);
                mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mBackgroundHandler);

                // Find out if we need to swap dimension to get the preview size relative to sensor
                // coordinate.
                int displayRotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
                //noinspection ConstantConditions
                mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                boolean swappedDimensions = false;
                switch (displayRotation) {
                    case Surface.ROTATION_0:
                    case Surface.ROTATION_180:
                        if (mSensorOrientation == 90 || mSensorOrientation == 270) {
                            swappedDimensions = true;
                        }
                        break;
                    case Surface.ROTATION_90:
                    case Surface.ROTATION_270:
                        if (mSensorOrientation == 0 || mSensorOrientation == 180) {
                            swappedDimensions = true;
                        }
                        break;
                    default:
                        Log.e(TAG, "Display rotation is invalid: " + displayRotation);
                }

                Point displaySize = new Point();
                mActivity.getWindowManager().getDefaultDisplay().getSize(displaySize);
                int rotatedPreviewWidth = width;
                int rotatedPreviewHeight = height;
                int maxPreviewWidth = displaySize.x;
                int maxPreviewHeight = displaySize.y;
                Log.d("log13",maxPreviewWidth+" "+maxPreviewHeight);

                if (swappedDimensions) {
                    rotatedPreviewWidth = height;
                    rotatedPreviewHeight = width;
                    maxPreviewWidth = displaySize.y;
                    maxPreviewHeight = displaySize.x;
                }

                // Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
                // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
                // garbage capture data.
                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                        rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth,
                        maxPreviewHeight, largest);

                // We fit the aspect ratio of TextureView to the size of preview we picked.
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                  Log.d("log17",mPreviewSize.getWidth()+" "+ mPreviewSize.getHeight());
                    mTextureView.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                } else {
                    Log.d("log18",mPreviewSize.getWidth()+" "+ mPreviewSize.getHeight());
                    mTextureView.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
                }

                // Check if the flash is supported.
                Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                mFlashSupported = available == null ? false : available;

                mCameraId = cameraId;
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
            CommonClass.showSnackbarMessage(rL_rootview, getString(R.string.camera_error));
        }
    }

    */
/**
 * Opens the camera specified by {@link Camera2Activity#mCameraId}.
 *//*

    private void openCamera(int width, int height) {
        System.out.println(TAG+" "+"openCamera called...");
        if (runTimePermission.checkPermissions(permissionsArray)) {
            setUpCameraOutputs(width, height);
            configureTransform(width, height);
//   configureTransform(reqWidth, reqHeight);
            CameraManager manager = (CameraManager) mActivity.getSystemService(Context.CAMERA_SERVICE);
            try {
                if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                    //throw new RuntimeException("Time out waiting to lock camera opening.");
                    System.out.println(TAG+" "+"Time out waiting to lock camera opening.");
                }
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                manager.openCamera(mCameraId, mStateCallback, mBackgroundHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
            }
        }
        else
            {
                runTimePermission.requestPermission();
                isToOpenCamera=false;
            }
    }

    */
/**
 * Starts a background thread and its {@link Handler}.
 *//*

    private void startBackgroundThread() {
        */
/*
      An additional thread for running tasks that shouldn't block the UI.
     *//*

        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    */
/**
 * Creates a new {@link CameraCaptureSession} for camera preview.
 *//*

    private void createCameraPreviewSession() {
        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;

            // We configure the size of default buffer to be the size of camera preview we want.
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

            // This is the output Surface we need to start preview.
            Surface surface = new Surface(texture);

            // We set up a CaptureRequest.Builder with the output Surface.
            mPreviewRequestBuilder
                    = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);

            // Here, we create a CameraCaptureSession for camera preview.
            mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            // The camera is already closed
                            if (null == mCameraDevice) {
                                return;
                            }
                            Log.d("log81","85");
                            // When the session is ready, we start displaying the preview.
                            mCaptureSession = cameraCaptureSession;

                            Log.d("log81","88");
                            // Auto focus should be continuous for camera preview.
                            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

                            Log.d("log81","87");
                            // Flash is automatically enabled when necessary.
                            setAutoFlash(mPreviewRequestBuilder);
                            Log.d("log81","86");
                            // Finally, we start displaying the camera preview.
                            mPreviewRequest = mPreviewRequestBuilder.build();

                            Log.d("log81","84");
                            try {


                                Log.d("log81","81");

                                mCaptureSession.setRepeatingRequest(mPreviewRequest,
                                        mCaptureCallback, mBackgroundHandler);
                            } catch (CameraAccessException e) {

                                e.printStackTrace();
                            }
                            finally {
                                if (mCameraOpenCloseLock!=null)
                                    mCameraOpenCloseLock.release();

                                */
/*if (mCameraDevice!=null)
                                    mCameraDevice.close();*//*

                            }
                        }

                        @Override
                        public void onConfigureFailed(
                                @NonNull CameraCaptureSession cameraCaptureSession) {
                            System.out.println(TAG+" "+"failed");
                        }
                    }, null
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    */
/**
 * Configures the necessary {@link android.graphics.Matrix} transformation to `mTextureView`.
 * This method should be called after the camera preview size is determined in
 * setUpCameraOutputs and also the size of `mTextureView` is fixed.
 *
 * @param viewWidth  The width of `mTextureView`
 * @param viewHeight The height of `mTextureView`
 *//*

    private void configureTransform(int viewWidth, int viewHeight) {
        if (null == mTextureView || null == mPreviewSize || null == mActivity) {
            return;
        }
        int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());

        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        mTextureView.setTransform(matrix);
        Log.d("log12999",mPreviewSize.getHeight()+" "+mPreviewSize.getWidth()+" "+viewHeight+" "+viewWidth);
    }

    */
/**
 * Lock the focus as the first step for a still image capture.
 *//*

    private void lockFocus() {
        try {
            // This is how to tell the camera to lock focus.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the lock.
            mState = STATE_WAITING_LOCK;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (mCameraOpenCloseLock!=null)
                mCameraOpenCloseLock.release();
        }
    }

    */
/**
 * Run the precapture sequence for capturing a still image. This method should be called when
 * we get a response in {@link #mCaptureCallback} from {@link #lockFocus()}.
 *//*

    private void runPrecaptureSequence() {
        try {
            // This is how to tell the camera to trigger.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the precapture sequence to be set.
            mState = STATE_WAITING_PRECAPTURE;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        finally {
            if (mCameraOpenCloseLock!=null)
                mCameraOpenCloseLock.release();
        }
    }

    */
/**
 * Capture a still picture. This method should be called when we get a response in
 * {@link #mCaptureCallback} from both {@link #lockFocus()}.
 *//*

    private void captureStillPicture() {
        try {
            if (null == mActivity || null == mCameraDevice)
            {
                return;
            }
            // This is the CaptureRequest.Builder that we use to take a picture.
            final CaptureRequest.Builder captureBuilder =
                    mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReader.getSurface());
            // Use the same AE and AF modes as the preview.
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            setAutoFlash(captureBuilder);
            // Orientation
            int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation(rotation));
            CameraCaptureSession.CaptureCallback CaptureCallback = new CameraCaptureSession.CaptureCallback()
            {

                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                               @NonNull CaptureRequest request,
                                               @NonNull TotalCaptureResult result) {
                    unlockFocus();
                }
            };

            mCaptureSession.stopRepeating();
            mCaptureSession.capture(captureBuilder.build(), CaptureCallback, null);
        } catch (CameraAccessException e)
        {
            e.printStackTrace();
        }
    }

    */
/**
 * Retrieves the JPEG orientation from the specified screen rotation.
 *
 * @param rotation The screen rotation.
 * @return The JPEG orientation (one of 0, 90, 270, and 360)
 *//*

    private int getOrientation(int rotation) {
        // Sensor orientation is 90 for most devices, or 270 for some devices (eg. Nexus 5X)
        // We have to take that into account and rotate JPEG properly.
        // For devices with orientation of 90, we simply return our mapping from ORIENTATIONS.
        // For devices with orientation of 270, we need to rotate the JPEG 180 degrees.
        return (ORIENTATIONS.get(rotation) + mSensorOrientation + 270) % 360;
    }

    */
/**
 * Unlock the focus. This method should be called when still image capture sequence is
 * finished.
 *//*

    private void unlockFocus() {
        try {
            // Reset the auto-focus trigger
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            setAutoFlash(mPreviewRequestBuilder);
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
            // After this, the camera will go back to the normal state of preview.
            mState = STATE_PREVIEW;

            Log.d("log81","82");
            mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        finally {
            if (mCameraOpenCloseLock!=null)
                mCameraOpenCloseLock.release();
        }
    }

    private void setAutoFlash(CaptureRequest.Builder requestBuilder) {
        if (mFlashSupported) {
            requestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
        }
    }

    */
/**
 * Saves a JPEG {@link Image} into the specified {@link File}.
 *//*

    private class ImageSaver implements Runnable
    {
        */
/**
 * The JPEG image
 *//*

        private final Image mImage;
        */
/**
 * The file we save the image into.
 *//*

        private final File mFile;

        ImageSaver(Image image, File file) {
            mImage = image;
            mFile = file;
        }

        @Override
        public void run() {
            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            //saveCapturedImage(bytes);

            buffer.get(bytes);
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(mFile);
                output.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mImage.close();
                if (null != output) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    */
/**
 * Compares two {@code Size}s based on their areas.
 *//*

    private class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            // back button
            case R.id.rL_cancel_btn:
                onBackPressed();
                break;

            // gallery
            case R.id.iV_gallery:{
                System.out.println(TAG+" "+"captured image size="+arrayListImgPath.size());

                intent = new Intent(mActivity, GalleryActivity.class);
                ArrayList<String> galleryImagePaths = new ArrayList<>();
                ArrayList<Integer> rotationAngles=new ArrayList<>();

                for (int i = 0; i < arrayListImgPath.size(); i++) {
                    galleryImagePaths.add(arrayListImgPath.get(i).getImagePath());
                    rotationAngles.add(arrayListImgPath.get(i).getRotateAngle());
                }

                intent.putExtra("arrayListImgPath", galleryImagePaths);
                intent.putExtra("rotationAngles",rotationAngles);
                startActivityForResult(intent, VariableConstants.SELECT_GALLERY_IMG_REQ_CODE);
                break;
            }

            // done
            case R.id.rL_done :
                ArrayList<String> galleryImagePaths=new ArrayList<>();
                ArrayList<Integer> rotationAngles=new ArrayList<>();

                for(int i=0;i<arrayListImgPath.size();i++)
                {
                    galleryImagePaths.add(arrayListImgPath.get(i).getImagePath());
                    rotationAngles.add(arrayListImgPath.get(i).getRotateAngle());
                }

                if (isFromUpdatePost)
                {
                    intent =new Intent();
                    intent.putExtra("arrayListImgPath",galleryImagePaths);
                    intent.putExtra("rotationAngles",rotationAngles);
                    setResult(VariableConstants.UPDATE_IMAGE_REQ_CODE,intent);
                    onBackPressed();
                }
                else
                {
                    if (arrayListImgPath.size()>0)
                    {
                        intent=new Intent(mActivity,PostProductActivity.class);
                        intent.putExtra("arrayListImgPath",galleryImagePaths);
                        intent.putExtra("rotationAngles",rotationAngles);
                        startActivity(intent);
                    }
                }
                break;

            // flash light
            case R.id.iV_flashIcon :
                */
/*if (isFlashOn) {
                    // turn off flash
                    turnOffFlash();
                } else {
                    // turn on flash
                    turnOnFlash();
                }*//*

                break;
        }
    }

    @Override
    public void onBackPressed()
    {
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case VariableConstants.PERMISSION_REQUEST_CODE :
                System.out.println("grant result="+grantResults.length);
                if (grantResults.length>0)
                {
                    for (int count=0;count<grantResults.length;count++)
                    {
                        if (grantResults[count]!= PackageManager.PERMISSION_GRANTED)
                            runTimePermission.allowPermissionAlert(permissions[count]);

                    }
                    System.out.println("isAllPermissionGranted="+runTimePermission.checkPermissions(permissionsArray));
                    if (runTimePermission.checkPermissions(permissionsArray))
                    {
                        isToOpenCamera = true;
                        System.out.println(TAG+" "+"openCamera from onRequestPermissionsResult");
                        openCamera(mTextureView.getWidth(),mTextureView.getHeight());
                    }
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(TAG+" "+"on activity result called..."+requestCode);
        if (data != null)
        {
            switch (requestCode)
            {
                case VariableConstants.SELECT_GALLERY_IMG_REQ_CODE :
                    if (data.getStringArrayListExtra("arrayListImgPath")!=null && data.getStringArrayListExtra("arrayListImgPath").size()>0)
                    {
                        tV_upload.setTextColor(ContextCompat.getColor(mActivity,R.color.pink_color));
                        arrayListImgPath.clear();

                        ArrayList<String > imagePathsFromGallery=data.getStringArrayListExtra("arrayListImgPath");
                        CapturedImage image;


                        for(int i=0;i<imagePathsFromGallery.size();i++) {
                            image = new CapturedImage();

                            image.setRotateAngle(0);
                            image.setImagePath(imagePathsFromGallery.get(i));
                            arrayListImgPath.add(image);
                        }
//                        arrayListImgPath.addAll(data.getStringArrayListExtra("arrayListImgPath"));
                        imagesHorizontalRvAdap.notifyDataSetChanged();
                    }
                    else  tV_upload.setTextColor(ContextCompat.getColor(mActivity,R.color.reset_button_bg));
                    break;
            }
        }
    }
}
*/
