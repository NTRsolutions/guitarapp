package com.yelo.com.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import com.yelo.com.R;
import com.yelo.com.adapter.GalleryImgHorizontalRvAdap;
import com.yelo.com.adapter.GalleryRvAdap;
import com.yelo.com.fcm_push_notification.Config;
import com.yelo.com.fcm_push_notification.NotificationMessageDialog;
import com.yelo.com.fcm_push_notification.NotificationUtils;
import com.yelo.com.pojo_class.GalleryImagePojo;
import com.yelo.com.utility.ItemDecorationRvItems;
import com.yelo.com.utility.RunTimePermission;
import com.yelo.com.utility.VariableConstants;
import java.util.ArrayList;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * <h>GalleryActivity</h>
 * <p>
 *     In this class we used to retrieve the all images from gallery and show in
 *     grids. Once user clicks on any image then mark and an image selected and
 *     shows all selected images below.
 * </p>
 * @since 20-Jun-17
 */
public class GalleryActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = GalleryActivity.class.getSimpleName();
    private String[] permissionsArray;
    private RunTimePermission runTimePermission;
    private  GalleryRvAdap galleryRvAdap;
    private ArrayList<GalleryImagePojo> arrayListGalleryImg;
    ArrayList<String> aL_cameraImgPath;
    private ArrayList<Integer> rotationAngles=new ArrayList<>();
    public GalleryImgHorizontalRvAdap imagesHorizontalRvAdap;
    public RelativeLayout rL_rootview;
    private NotificationMessageDialog mNotificationMessageDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

        initVariable();
    }

    /**
     * <h>initVariable</h>
     * <p>
     *     In this method we used to initialize the all variables.
     * </p>
     */
    private void initVariable()
    {
        Activity mActivity = GalleryActivity.this;
        mNotificationMessageDialog=new NotificationMessageDialog(mActivity);
        arrayListGalleryImg=new ArrayList<>();

        // receive images from last activity
        Intent intent=getIntent();
        aL_cameraImgPath=intent.getStringArrayListExtra("arrayListImgPath");
        rotationAngles=intent.getIntegerArrayListExtra("rotationAngles");

        for (String path : aL_cameraImgPath)
        {
            System.out.println(TAG+" "+"path="+path);
        }

        // back button
        RelativeLayout rL_back_btn= (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);

        // camera icon
        RelativeLayout rL_camera_icon= (RelativeLayout) findViewById(R.id.rL_camera_icon);
        rL_camera_icon.setOnClickListener(this);

        // initialize permission array
        permissionsArray=new String[]{WRITE_EXTERNAL_STORAGE};
        runTimePermission=new RunTimePermission(mActivity,permissionsArray,true);

        // set adapter
        galleryRvAdap=new GalleryRvAdap(mActivity,arrayListGalleryImg,aL_cameraImgPath);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(mActivity,3);
        RecyclerView rV_galleryImg = (RecyclerView) findViewById(R.id.rV_galleryImg);
        rV_galleryImg.setLayoutManager(gridLayoutManager);

        // set space equility between recycler view items
        int spanCount = 3; // 3 columns
        int spacing = 5; // 50px
        rV_galleryImg.addItemDecoration(new ItemDecorationRvItems(spanCount, spacing, true));

        rV_galleryImg.setAdapter(galleryRvAdap);

        // retrieve images from gallery
        if (runTimePermission.checkPermissions(permissionsArray))
        {
            if (loadPhotosFromNativeGallery()!=null && loadPhotosFromNativeGallery().size()>0)
            {
                galleryRvAdap.notifyDataSetChanged();
            }
        }
        else runTimePermission.requestPermission();

        // set adapter for horizontal images
        rL_rootview= (RelativeLayout) findViewById(R.id.rL_rootview);
        RelativeLayout rL_images = (RelativeLayout) findViewById(R.id.rL_images);

        if (aL_cameraImgPath!=null && aL_cameraImgPath.size()>0)
            rL_images.setVisibility(View.VISIBLE);

        imagesHorizontalRvAdap=new GalleryImgHorizontalRvAdap(mActivity,aL_cameraImgPath,galleryRvAdap,arrayListGalleryImg,rotationAngles);
        RecyclerView rV_capturedImages = (RecyclerView) findViewById(R.id.rV_capturedImages);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(mActivity,LinearLayoutManager.HORIZONTAL,false);
        rV_capturedImages.setLayoutManager(linearLayoutManager);
        rV_capturedImages.setAdapter(imagesHorizontalRvAdap);

        // Done button
        RelativeLayout rL_done= (RelativeLayout) findViewById(R.id.rL_done);
        rL_done.setOnClickListener(this);
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

    /**
     * <h>loadPhotosFromNativeGallery</h>
     * <p>
     *     In this method we used to retrieve all gallery images from device and store all
     *     images into a list.
     * </p>
     * @return The list consisting of all images path
     */
    private ArrayList<String> loadPhotosFromNativeGallery()
    {
        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        Cursor imagecursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy + " DESC");

        ArrayList<String> imageUrls = new ArrayList<>();

        for (int i = 0; i < imagecursor.getCount(); i++)
        {
            imagecursor.moveToPosition(i);
            int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
            imageUrls.add(imagecursor.getString(dataColumnIndex));
            GalleryImagePojo galleryImagePojo=new GalleryImagePojo();
            galleryImagePojo.setGalleryImagePath(imagecursor.getString(dataColumnIndex));
            arrayListGalleryImg.add(galleryImagePojo);
            System.out.println("=====> Array path => "+imageUrls.get(i)+" "+"size="+imageUrls.size());
        }

        if (arrayListGalleryImg.size()>0)
        {
            for (String cameraImgPath : aL_cameraImgPath)
            {
                for (GalleryImagePojo galleryImagePojo : arrayListGalleryImg)
                {
                    if (cameraImgPath.equals(galleryImagePojo.getGalleryImagePath()))
                    {
//                        galleryImagePojo.setSelected(true);
                        galleryImagePojo.setRotationAngle(rotationAngles.get(0));
                    }
                }
            }
        }
        return imageUrls;
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
                        //imageUrls = loadPhotosFromNativeGallery();
                        if (loadPhotosFromNativeGallery()!=null && loadPhotosFromNativeGallery().size()>0)
                        {
                            galleryRvAdap.notifyDataSetChanged();
                        }
                    }
                }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            // back button
            case R.id.rL_back_btn :
                onBackPressed();
                finish();
                break;

            // done button
            case R.id.rL_done :
                moveToCameraScreen1();
                break;

            // camera icon
            case R.id.rL_camera_icon :
                moveToCameraScreen();
                break;
        }
    }

    /**
     * <h>MoveToCameraScreen</h>
     * <p>
     *     In this method we used to move to the previous activity i.e CameraActivity class and
     *     pass the images list.
     * </p>
     */
    private void moveToCameraScreen()
    {
        Intent intent=new Intent();
//        intent.putExtra("arrayListImgPath",aL_cameraImgPath);
//        setResult(VariableConstants.SELECT_GALLERY_IMG_REQ_CODE,intent);
        onBackPressed();
        finish();
    }


    private void moveToCameraScreen1()
    {
        Intent intent=new Intent();
        intent.putExtra("arrayListImgPath",aL_cameraImgPath);
        setResult(VariableConstants.SELECT_GALLERY_IMG_REQ_CODE,intent);
        onBackPressed();
        finish();
    }

}
