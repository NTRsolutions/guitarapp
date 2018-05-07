package com.yelo.com.main.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yelo.com.R;
import com.yelo.com.adapter.StoreImagesHorizontalRvAdapter;
import com.yelo.com.device_camera.HandleCameraEvents;
import com.yelo.com.utility.RunTimePermission;
import com.yelo.com.utility.VariableConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import co.simplecrop.android.simplecropimage.CropImage;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.yelo.com.utility.VariableConstants.TEMP_PHOTO_FILE_NAME;

public class MarketOwnerActivity  extends AppCompatActivity {

    RecyclerView mRvImages;
    ImageView mIvCamera;
    StoreImagesHorizontalRvAdapter adapter;
    EditText mEdStoreName, EdPhoneNumber, EdAddress;
    List<String> mListPath = new ArrayList<>( );
    private String[] permissionsArray;
    private RunTimePermission runTimePermission;
    private HandleCameraEvents mHandleCameraEvents;
    private File mFile;
    String state;

    RelativeLayout mRlCreate;

    private static final String TAG = MarketOwnerActivity.class.getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        setContentView( R.layout.activity_market_store_records);
        initVariable();
        clickListner();
        setadpater();

    }

    private void initVariable(){

        try {
            mRvImages = findViewById( R.id.recycler_view_store_images );
            mIvCamera  = findViewById( R.id.iV_camera_click );

            mEdStoreName = findViewById( R.id.eT_store_name );
            EdPhoneNumber = findViewById( R.id.eT_mobileNo );
            EdAddress = findViewById( R.id.eT_address );

            mRlCreate = findViewById( R.id.rL_create_store );


             state = Environment.getExternalStorageState();

            if (Environment.MEDIA_MOUNTED.equals(state))
            {
                mFile = new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME+System.currentTimeMillis());
            }
            else {
                mFile = new File(getFilesDir(), TEMP_PHOTO_FILE_NAME+System.currentTimeMillis());
            }

            mHandleCameraEvents=new HandleCameraEvents(this,mFile);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private void clickListner(){

        try {

            mIvCamera.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    permissionsArray =new String[]{CAMERA,WRITE_EXTERNAL_STORAGE};
                    runTimePermission=new RunTimePermission(MarketOwnerActivity.this, permissionsArray,false);
                    if (runTimePermission.checkPermissions(permissionsArray))
                        chooseImage();
                    else
                    {
                        runTimePermission.requestPermission();
                    }
                }
            } );

            mRlCreate.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onBackPressed();
                }
            } );

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void setadpater(){
        if(adapter == null){

            adapter=new StoreImagesHorizontalRvAdapter(MarketOwnerActivity.this,mListPath);
            RecyclerView rV_cameraImages = (RecyclerView) findViewById(R.id.rV_cameraImages);
            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
            mRvImages.setLayoutManager(linearLayoutManager);
            mRvImages.setAdapter(adapter);
        }else {
            adapter.notifyDataSetChanged();
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(TAG+" "+"on activity result..."+" "+"requestCode="+requestCode+" "+"result code="+requestCode+"data="+data);


        Bitmap bitmap;

        switch (requestCode) {
            // Gallery
            case VariableConstants.SELECT_GALLERY_IMG_REQ_CODE:


                if (Environment.MEDIA_MOUNTED.equals(state))
                {
                    mFile = new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME+System.currentTimeMillis());
                }
                else {
                    mFile = new File(getFilesDir(), TEMP_PHOTO_FILE_NAME+System.currentTimeMillis());
                }

                mHandleCameraEvents=new HandleCameraEvents(this,mFile);

                if (resultCode != RESULT_OK) {
                    return;
                }
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());

                    FileOutputStream fileOutputStream = new FileOutputStream(mFile);
                    mHandleCameraEvents.copyStream(inputStream, fileOutputStream);
                    fileOutputStream.close();
                    assert inputStream != null;
                    inputStream.close();
                    mHandleCameraEvents.startCropImage();

                } catch (Exception e) {

                    Log.e(TAG, "Error while creating temp file", e);
                }
                break;

            // Camera
            case VariableConstants.CAMERA_CAPTURE:
                if (resultCode != RESULT_OK) {
                    return;
                }
                mHandleCameraEvents.startCropImage();
                break;

            // Crop
            case VariableConstants.PIC_CROP:
                if(data==null){
                    return;
                }
                String path = data.getStringExtra( CropImage.IMAGE_PATH);
                if (path == null) {
                    return;
                }

                mListPath.add( path );
                setadpater();

                break;

            // To finish Current screen

        }
    }



    public void chooseImage()
    {
        final Dialog selectImgDialog=new Dialog(this);
        selectImgDialog.getWindow().requestFeature( Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(new ColorDrawable( Color.TRANSPARENT));
        selectImgDialog.setContentView(R.layout.select_image_layout);

        // Take picture
        RelativeLayout rL_take_pic= (RelativeLayout) selectImgDialog.findViewById(R.id.rL_take_pic);
        rL_take_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImgDialog.dismiss();
                mHandleCameraEvents.takePicture();
            }
        });

        // Choose Image from gallery
        RelativeLayout rL_select_pic= (RelativeLayout) selectImgDialog.findViewById(R.id.rL_select_pic);
        rL_select_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImgDialog.dismiss();
                mHandleCameraEvents.openGallery();
            }
        });

        // Remove profile pic

        // Remove profile pic
        RelativeLayout rL_remove_pic= (RelativeLayout) selectImgDialog.findViewById(R.id.rL_remove_pic);
        rL_remove_pic.setVisibility( View.GONE );

        selectImgDialog.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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

                    if (runTimePermission.checkPermissions(permissionsArray))
                    {
                            chooseImage();
                    }
                }
        }
    }
}
