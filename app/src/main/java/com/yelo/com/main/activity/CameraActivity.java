package com.yelo.com.main.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.yelo.com.R;
import com.yelo.com.adapter.ImagesHorizontalRvAdap;
import com.yelo.com.fcm_push_notification.Config;
import com.yelo.com.fcm_push_notification.NotificationMessageDialog;
import com.yelo.com.fcm_push_notification.NotificationUtils;
import com.yelo.com.utility.CapturedImage;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.RunTimePermission;
import com.yelo.com.utility.VariableConstants;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * <h>CameraActivity</h>
 * <p>
 *     In this class we used to do the custom camera funtionality on surface view. we
 *     have a custom button to capture image. Once we capture image we save the path
 *     of image into list from File. In this we have maximum 5 picture to take either
 *     by camera or gallery.
 * </p>
 * @since 5/2/2017
 * @author 3Embed
 * @version 1.0
 */
public class CameraActivity extends AppCompatActivity implements View.OnClickListener, SurfaceHolder.Callback
{
    private static final String TAG = CameraActivity.class.getSimpleName();
    private Camera camera;
    private SurfaceHolder surfaceHolder;
    private Camera.PictureCallback jpegCallback;
    private Activity mActivity;
    private String[] permissionsArray;
    private RunTimePermission runTimePermission;
    private ArrayList<CapturedImage> arrayListImgPath;
    public boolean isToCaptureImage;
    private ProgressBar pBar_captureImg;
    private RelativeLayout rL_rootview;
    public TextView tV_upload;
    private ImagesHorizontalRvAdap imagesHorizontalRvAdap;
    private boolean isFlashOn,isFromUpdatePost;
    private ImageView iV_flashIcon;
    private Camera.Parameters param;
    private NotificationMessageDialog mNotificationMessageDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        overridePendingTransition(R.anim.slide_up, R.anim.stay );

        initVariables();
    }

    /**
     * <h>InitVariables</h>
     * <p>
     *     In this method we used to initialize all xml variables and data member.
     * </p>
     */
    private void initVariables()
    {
        mActivity=CameraActivity.this;
        mNotificationMessageDialog=new NotificationMessageDialog(mActivity);
        isToCaptureImage=true;
        arrayListImgPath=new ArrayList<>();

        // receive datas from last activity
        Intent intent=getIntent();
        isFromUpdatePost=intent.getBooleanExtra("isUpdatePost",false);

        // set adpter for horizontal images
        tV_upload= (TextView) findViewById(R.id.tV_upload);
        iV_flashIcon= (ImageView) findViewById(R.id.iV_flashIcon);
        iV_flashIcon.setOnClickListener(this);

        imagesHorizontalRvAdap=new ImagesHorizontalRvAdap(mActivity,arrayListImgPath,tV_upload,isToCaptureImage);
        RecyclerView rV_cameraImages = (RecyclerView) findViewById(R.id.rV_cameraImages);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(mActivity,LinearLayoutManager.HORIZONTAL,false);
        rV_cameraImages.setLayoutManager(linearLayoutManager);
        rV_cameraImages.setAdapter(imagesHorizontalRvAdap);

        // set status bar color
        CommonClass.statusBarColor(mActivity);
        permissionsArray=new String[]{CAMERA,WRITE_EXTERNAL_STORAGE};
        runTimePermission=new RunTimePermission(mActivity,permissionsArray,true);

        pBar_captureImg= (ProgressBar) findViewById(R.id.pBar_captureImg);
        pBar_captureImg.setVisibility(View.GONE);
        SurfaceView surfaceView = (SurfaceView)findViewById(R.id.surfaceView);
        RelativeLayout rL_done = (RelativeLayout)findViewById(R.id.rL_done);
        rL_done.setOnClickListener(this);
        surfaceHolder = surfaceView.getHolder();
        rL_rootview= (RelativeLayout) findViewById(R.id.rL_rootview);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        surfaceHolder.addCallback(this);

        // deprecated setting, but required on Android versions prior to 3.0
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        jpegCallback = new Camera.PictureCallback()
        {
            public void onPictureTaken(byte[] data, Camera camera)
            {
                try {
                    camera.takePicture(null, null, pictureCallback);
                }
                catch (Exception e)
                {e.printStackTrace();



                    isToCaptureImage=true;
                    pBar_captureImg.setVisibility(View.GONE);
                    CommonClass.showTopSnackBar(rL_rootview,e.getMessage());
                }
                //saveCapturedImage(data);
            }
        };

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
                    //take the picture
                    if (isToCaptureImage)
                    {
                        //   pBar_captureImg.setVisibility(View.VISIBLE);
                        //camera.takePicture(shutterCallback, null, jpegCallback);
                        camera.takePicture(null, null, jpegCallback);
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


    @Override
    protected void onResume()
    {
        super.onResume();

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
        super.onPause();
    }

    // play sound while capturing image
    private final Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            mgr.playSoundEffect(AudioManager.FLAG_PLAY_SOUND);
        }
    };

    Camera.PictureCallback pictureCallback = new Camera.PictureCallback()
    {
        @Override
        public void onPictureTaken(byte[] data, Camera camera)
        {
            saveCapturedImage(data);
        }
    };

    /**
     * <h>saveCapturedImage</h>
     * <p>
     *     In this method we used to save the captured image into file.
     * </p>
     * @param data The image data-array
     */
    private void saveCapturedImage(byte[] data)
    {
        // File imageFile;
        try {
            String folderPath;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            {
                folderPath=Environment.getExternalStorageDirectory() + "/" + getResources().getString(R.string.app_name);
            } else {
                folderPath=mActivity.getFilesDir() + "/" + getResources().getString(R.string.app_name);
            }

            File folder = new File(folderPath);

            if (!folder.exists() && !folder.isDirectory()) {
                folder.mkdirs();
            }

            String name =String.valueOf(System.currentTimeMillis())+".jpg";

            File imageFile = new File(folderPath, name );

            try {
                if (!imageFile.exists()) {

                    imageFile.createNewFile();
                }
            }catch(Exception e){
                Toast.makeText(getBaseContext(), "Image Not saved", Toast.LENGTH_SHORT).show();
                return;
            }

            FileOutputStream file_out = new FileOutputStream(imageFile);
            file_out.write(data);
            file_out.close();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.MediaColumns.DATA, imageFile.getAbsolutePath());
            getApplicationContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            // Code For Captured Image Save in a ImageView.
            final  String imagePath = folderPath + File.separator + name ;
            mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                    pBar_captureImg.setVisibility(View.GONE);
                    System.out.println(TAG+" "+"image path="+imagePath);
                    CapturedImage image=new CapturedImage();
                    image.setImagePath(imagePath);
                    image.setRotateAngle(getRoatationAngle(Camera.CameraInfo.CAMERA_FACING_FRONT));
                    arrayListImgPath.add(image);
                    imagesHorizontalRvAdap.notifyItemInserted(arrayListImgPath.size());
                    refreshCamera();
                    isToCaptureImage = arrayListImgPath.size() < 5;

                    // enable upload button
                    if (arrayListImgPath.size()>0)
                        tV_upload.setTextColor(ContextCompat.getColor(mActivity,R.color.pink_color));
                }
            });

            // mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <h>RefreshCamera</h>
     * <p>
     *     In this method we just refresh the custom camera by
     *     using startPreview() method.
     * </p>
     */
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
            e.printStackTrace();
        }
    }

    /**
     * Get Rotation Angle
     *
     * @param cameraId
     *            probably front cam
     * @return angel to rotate
     */
    public  int getRoatationAngle( int cameraId) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    @Override
    public void onBackPressed()
    {
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
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

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        System.out.println(TAG+" "+"camera="+"surfaceCreated called...");
        if (runTimePermission.checkPermissions(permissionsArray)) {

            openCameraPreview();
        } else {
            runTimePermission.requestPermission();
        }
    }

    private void openCameraPreview()
    {
        try {
            // open the camera
            camera = Camera.open();
        } catch (RuntimeException e) {
            // check for exceptions
            System.err.println(e.getMessage());
            return;
        }
        param = camera.getParameters();

        // modify parameter
        param.setPreviewSize(352, 288);

        try {
            camera.setParameters(param);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        try {
            // The Surface has been created, now tell the camera where to draw
            // the preview.
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
            // check for exceptions
            System.err.println(e.getMessage());
        }
    }

    /*
 * Turning On flash
 */
    private void turnOnFlash() {
        if (!isFlashOn) {
            if (camera == null || param == null) {
                return;
            }
            // play sound
            param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(param);
            //camera.startPreview();
            isFlashOn = true;

            // changing button/switch image
            toggleButtonImage();
        }
    }

    /*
     * Turning Off flash
     */
    private void turnOffFlash() {
        if (isFlashOn) {
            if (camera == null || param == null) {
                return;
            }
            param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(param);
            //camera.stopPreview();
            isFlashOn = false;

            // changing button/switch image
            toggleButtonImage();
        }
    }

    /*
 * Toggle switch button images changing image states to on / off
 */
    private void toggleButtonImage() {
        if (isFlashOn) {
            iV_flashIcon.setImageResource(R.drawable.flash_light_on);
        } else {
            iV_flashIcon.setImageResource(R.drawable.flash_light_off);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (runTimePermission.checkPermissions(permissionsArray)) {

            System.out.println(TAG+" "+"camera="+"surfaceChanged called...");
            // Now that the size is known, set up the camera parameters and begin
            // the preview.
            if (camera!=null)
                camera.setDisplayOrientation(90);
            refreshCamera();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (runTimePermission.checkPermissions(permissionsArray)) {

            System.out.println(TAG + " " + "camera=" + "surfaceDestroyed called...");
            // stop preview and release camera
            camera.stopPreview();
            camera.release();
            camera = null;
        }
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
                        openCameraPreview();
                        camera.setDisplayOrientation(90);
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
