package com.yelo.com.main.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.yelo.com.R;
import com.yelo.com.device_camera.HandleCameraEvents;
import com.yelo.com.event_bus.BusProvider;
import com.yelo.com.fcm_push_notification.Config;
import com.yelo.com.fcm_push_notification.NotificationMessageDialog;
import com.yelo.com.fcm_push_notification.NotificationUtils;
import com.yelo.com.pojo_class.CloudData;
import com.yelo.com.pojo_class.EditProfilePojo;
import com.yelo.com.pojo_class.cloudinary_details_pojo.Cloudinary_Details_reponse;
import com.yelo.com.pojo_class.edit_profile_pojo.EditProfileData;
import com.yelo.com.pojo_class.profile_pojo.ProfileResultDatas;
import com.yelo.com.utility.ApiUrl;
import com.yelo.com.utility.CircleTransform;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.DialogBox;
import com.yelo.com.utility.OkHttp3Connection;
import com.yelo.com.utility.RunTimePermission;
import com.yelo.com.utility.SessionManager;
import com.yelo.com.utility.UploadToCloudinary;
import com.yelo.com.utility.VariableConstants;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;
import co.simplecrop.android.simplecropimage.CropImage;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.yelo.com.utility.VariableConstants.CHANGE_LOC_REQ_CODE;
import static com.yelo.com.utility.VariableConstants.TEMP_PHOTO_FILE_NAME;
import static com.yelo.com.utility.VariableConstants.VERIFY_EMAIL_REQ_CODE;

/**
 * <h>EditProfileActivity</h>
 * <p>
 *     This class is called from MyProfileFrag class. In this class firstly we do
 *     api call to get user complete information. We have all the field editable
 *     to mobify last datas with new one.
 * </p>
 * @since 4/14/2017
 */
public class EditProfileActivity extends Activity implements View.OnClickListener
{
    private static final String TAG = EditProfileActivity.class.getSimpleName();
    private Activity mActivity;
    private EditText eT_userName,eT_fullName,eT_website,eT_bio;
    private HandleCameraEvents mHandleCameraEvents;
    private ImageView iV_profile_pic,iv_edit_icon;
    private String profilePicUrl="";
    private SessionManager mSessionManager;
    private RelativeLayout linear_rootElement;
    private RelativeLayout rL_pBar;
    private TextView tV_save,tV_gender,tV_mobileNo,tV_location,tV_emailId;
    private DialogBox mDialogBox;
    private ScrollView scrollView_editProfile;
    private ProgressBar pBar_editProfile;
    public static EditProfileActivity editProfileInstance;
    private boolean isToStartActivity;

    // run time permission
    private RunTimePermission runTimePermission;
    private String[] permissionsArray;
    private boolean isPictureTaken;
    private File mFile;
    private NotificationMessageDialog mNotificationMessageDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        BusProvider.getInstance().register(this);

        initVariables();
    }

    /**
     * <h>InitVariables</h>
     * <p>
     *     In this method we used to initialize all the xml variables.
     * </p>
     */
    private void initVariables()
    {
        isPictureTaken=false;
        isToStartActivity = true;
        mActivity=EditProfileActivity.this;
        mNotificationMessageDialog=new NotificationMessageDialog(mActivity);
        editProfileInstance=EditProfileActivity.this;
        mDialogBox=new DialogBox(mActivity);
        CommonClass.statusBarColor(mActivity);

        // progress bar
        rL_pBar= (RelativeLayout) findViewById(R.id.rL_pBar);
        rL_pBar.setVisibility(View.GONE);

        // gender
        tV_gender= (TextView) findViewById(R.id.tV_gender);
        RelativeLayout rL_select_gender = (RelativeLayout) findViewById(R.id.rL_select_gender);
        rL_select_gender.setOnClickListener(this);
        mSessionManager=new SessionManager(mActivity);
        iV_profile_pic= (ImageView) findViewById(R.id.iV_profile_pic);
        iv_edit_icon= (ImageView) findViewById(R.id.iv_edit_icon);
        scrollView_editProfile= (ScrollView) findViewById(R.id.scrollView_editProfile);
        scrollView_editProfile.setVisibility(View.GONE);
        permissionsArray=new String[]{CAMERA,WRITE_EXTERNAL_STORAGE};
        runTimePermission=new RunTimePermission(mActivity,permissionsArray,false);
        pBar_editProfile= (ProgressBar) findViewById(R.id.pBar_editProfile);

        LinearLayout rL_back_btn = findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);

        linear_rootElement= (RelativeLayout) findViewById(R.id.linear_rootElement);

        iV_profile_pic.setOnClickListener(this);
        iV_profile_pic.getLayoutParams().width = CommonClass.getDeviceWidth(mActivity)/4;
        iV_profile_pic.getLayoutParams().height = CommonClass.getDeviceWidth(mActivity)/4;

        // User name
        eT_userName= (EditText) findViewById(R.id.eT_userName);

        //..While user enter space then remove
        eT_userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(eT_userName.getText().toString().contains(" ")){
                    eT_userName.setText(eT_userName.getText().toString().replace(" ",""));
                    eT_userName.setSelection(eT_userName.getText().toString().length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // Full name
        eT_fullName= (EditText) findViewById(R.id.eT_fullName);

        // Email
        tV_emailId= (TextView) findViewById(R.id.tV_emailId);
        tV_emailId.setOnClickListener(this);

        // Mobile number
        tV_mobileNo = (TextView) findViewById(R.id.tV_mobileNo);
        tV_mobileNo.setOnClickListener(this);

        // website
        eT_website= (EditText) findViewById(R.id.eT_website);

        // location
        tV_location= (TextView) findViewById(R.id.tV_location);
        tV_location.setOnClickListener(this);

        // Bio
        eT_bio= (EditText) findViewById(R.id.eT_bio);

        // save
        tV_save = (TextView) findViewById(R.id.tV_save);
        tV_save.setOnClickListener(this);

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state))
        {
            mFile = new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME);
        }
        else {
            mFile = new File(getFilesDir(), TEMP_PHOTO_FILE_NAME);
        }
        mHandleCameraEvents = new HandleCameraEvents(mActivity,mFile);

        getProfileDatasApi();
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        isToStartActivity = true;

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
     * <h>GetProfileDatasApi</h>
     * <p>
     *     In this method we used to do api call to get user complete information.
     *     Once we get information then set all the field to the xml variables.
     * </p>
     */
    private void getProfileDatasApi()
    {
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            pBar_editProfile.setVisibility(View.VISIBLE);
            scrollView_editProfile.setVisibility(View.GONE);
            JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("token", mSessionManager.getAuthToken());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.EDIT_PROFILE, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag)
                {
                    System.out.println(TAG + " " + "edit profile res=" + result);

                    EditProfilePojo editProfilePojo;
                    Gson gson=new Gson();
                    editProfilePojo=gson.fromJson(result,EditProfilePojo.class);

                    switch (editProfilePojo.getCode())
                    {
                        // success
                        case "200" :
                            pBar_editProfile.setVisibility(View.GONE);
                            scrollView_editProfile.setVisibility(View.VISIBLE);
                            EditProfileData editProfileData=editProfilePojo.getData();
                            if (editProfileData!=null)
                            {
                                String fullName,username,websiteUrl,bio,email,phoneNumber,gender, place;
                                fullName=editProfileData.getFullName();
                                profilePicUrl=editProfileData.getProfilePicUrl();
                                username=editProfileData.getUsername();
                                websiteUrl=editProfileData.getWebsiteUrl();
                                bio=editProfileData.getBio();
                                email=editProfileData.getEmail();
                                phoneNumber=editProfileData.getPhoneNumber();
                                gender=editProfileData.getGender();
                                place=editProfileData.getPlace();
                                mSessionManager.setUserImage(profilePicUrl);

                                // Set user profile pic
                                if (profilePicUrl!=null && !profilePicUrl.isEmpty())
                                {
                                    isPictureTaken=false;
                                    iv_edit_icon.setVisibility(View.VISIBLE);
                                    Glide.with(mActivity)
                                            .load(profilePicUrl)
//                                            .transform(new CircleTransform())
                                            .placeholder(R.drawable.profile_bg)
                                            .into(iV_profile_pic);
                                }
                                else iv_edit_icon.setVisibility(View.VISIBLE);

                                // user name
                                if (username!=null && !username.isEmpty())
                                    eT_userName.setText(username);

                                // Full name
                                if (fullName!=null && !fullName.isEmpty())
                                    eT_fullName.setText(fullName);

                                // website
                                if (websiteUrl!=null && !websiteUrl.isEmpty())
                                    eT_website.setText(websiteUrl);

                                // bio
                                if (bio!=null && !bio.isEmpty())
                                    eT_bio.setText(bio);

                                // Email-Id
                                if (email!=null && !email.isEmpty())
                                    tV_emailId.setText(email);

                                // phone number
                                if (phoneNumber!=null && !phoneNumber.isEmpty())
                                    tV_mobileNo.setText(phoneNumber);

                                // address
                                if (place!=null && !place.isEmpty())
                                    tV_location.setText(place);

                                // gender
                                if (gender!=null && !gender.isEmpty())
                                    tV_gender.setText(gender);

                            }
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // error
                        default:
                            CommonClass.showSnackbarMessage(linear_rootElement,editProfilePojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    pBar_editProfile.setVisibility(View.GONE);
                }
            });
        }
        else CommonClass.showSnackbarMessage(linear_rootElement,getResources().getString(R.string.NoInternetAccess));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;

        Bitmap bitmap;
        switch (requestCode) {
            // Gallery
            case VariableConstants.SELECT_GALLERY_IMG_REQ_CODE:
                try
                {
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
                mHandleCameraEvents.startCropImage();
                break;

            // Crop
            case VariableConstants.PIC_CROP :
                if(data==null){
                    return;
                }
                String path = data.getStringExtra(CropImage.IMAGE_PATH);
                if (path == null)
                {
                    return;
                }
                isPictureTaken=true;
                iv_edit_icon.setVisibility(View.VISIBLE);
                bitmap = BitmapFactory.decodeFile(mFile.getPath());
                iV_profile_pic.setImageBitmap(mHandleCameraEvents.getCircleBitmap(bitmap));
                break;

            // change location
            case CHANGE_LOC_REQ_CODE :
                if (data!=null) {
                    System.out.println(TAG + " " + "data=" + data);
                    String userLat = data.getStringExtra("lat");
                    String userLng = data.getStringExtra("lng");
                    String address = data.getStringExtra("address");
                    tV_location.setText(address);
                    System.out.println(TAG + " " + "lat=" + userLat + " " + "lng=" + userLng + " " + "address=" + address);
                }
                break;

            // verify email
            case VERIFY_EMAIL_REQ_CODE :
                if (data!=null)
                {
                    String email=data.getStringExtra("emailId");
                    if (email!=null && !email.isEmpty())
                        tV_emailId.setText(email);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId())
        {
            // Back button
            case R.id.rL_back_btn :
                onBackPressed();
                break;

            // Change profile pic
            case R.id.iV_profile_pic :
                if (runTimePermission.checkPermissions(permissionsArray))
                    chooseImage();
                else runTimePermission.requestPermission();
                break;

            // select gander
            case R.id.rL_select_gender :
                mDialogBox.selectGender(tV_gender);
                break;

            // change mobile no
            case R.id.tV_mobileNo :
                if (isToStartActivity) {
                    intent = new Intent(mActivity, VerifyPhoneNoActivity.class);
                    startActivity(intent);
                    isToStartActivity = false;
                }
                break;

            // change email id
            case R.id.tV_emailId :
                if (isToStartActivity) {
                    intent = new Intent(mActivity, VerifyEmailIdActivity.class);
                    startActivityForResult(intent, VERIFY_EMAIL_REQ_CODE);
                    isToStartActivity = false;
                }
                break;

            // change location
            case R.id.tV_location :
                if (isToStartActivity) {
                    intent = new Intent(mActivity, ChangeLocationActivity.class);
                    startActivityForResult(intent, CHANGE_LOC_REQ_CODE);
                    isToStartActivity = false;
                }
                break;

            // Save
            case R.id.tV_save :

                try {
                    if(eT_userName.getText().toString().isEmpty()){
                        CommonClass.showSnackbarMessage(linear_rootElement,getString(R.string.empty_username));
                        break;
                    }
                    System.out.println(TAG+" "+"isPictureTaken="+isPictureTaken+" "+"profilePicUrl="+profilePicUrl);
                    if (isPictureTaken)
                    {
                        rL_pBar.setVisibility(View.VISIBLE);
                        tV_save.setVisibility(View.INVISIBLE);
                        getCloudinaryDetailsApi();
                    }
                    else
                    {
                        rL_pBar.setVisibility(View.VISIBLE);
                        tV_save.setVisibility(View.INVISIBLE);
                        updateProfileApi();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                break;
        }
    }

    /**
     * <h>SelectImage</h>
     * <p>
     *     Using this method we open a pop-up. when
     *     user click on profile image. it contains
     *     thee field Take Photo,Choose from Gallery,
     *     Cancel.
     * </p>
     */
    public void chooseImage()
    {
        final Dialog selectImgDialog=new Dialog(mActivity);
        selectImgDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mActivity.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
        RelativeLayout rL_remove_pic= (RelativeLayout) selectImgDialog.findViewById(R.id.rL_remove_pic);
        if (profilePicUrl.isEmpty())
        {
            iv_edit_icon.setVisibility(View.VISIBLE);
            rL_remove_pic.setVisibility(View.GONE);
        }
        else {
            iv_edit_icon.setVisibility(View.VISIBLE);
            rL_remove_pic.setVisibility(View.VISIBLE);
        }

        rL_remove_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeProfileImage();
                selectImgDialog.dismiss();
            }
        });

        selectImgDialog.show();
    }

    private void removeProfileImage()
    {
        profilePicUrl="";
        isPictureTaken=false;
        iv_edit_icon.setVisibility(View.VISIBLE);
        iV_profile_pic.setImageResource(R.drawable.profile_bg);
    }

    /**
     * <h2>getCloud_details</h2>
     * <p>
     * Collecting the cloudinary details from the server..
     * </P>
     */
    private void getCloudinaryDetailsApi() {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            JSONObject request_data = new JSONObject();
            OkHttp3Connection.doOkHttp3Connection("", ApiUrl.GET_CLOUDINARY_DETAILS, OkHttp3Connection.Request_type.POST, request_data, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String tag) {
                    Cloudinary_Details_reponse response = new Gson().fromJson(result, Cloudinary_Details_reponse.class);

                    if (response.getCode().equals("200"))
                    {
                        String cloudName, timestamp, apiKey, signature;
                        cloudName = response.getResponse().getCloudName();
                        timestamp = response.getResponse().getTimestamp();
                        apiKey = response.getResponse().getApiKey();
                        signature = response.getResponse().getSignature();

                        Bundle remaining_data = new Bundle();
                        remaining_data.putString("signature", signature);
                        remaining_data.putString("timestamp", timestamp);
                        remaining_data.putString("apiKey", apiKey);
                        remaining_data.putString("cloudName", cloudName);

                        postMedia(remaining_data);
                    } else {
                        CommonClass.showSnackbarMessage(linear_rootElement,response.getMessage());
                    }
                }

                @Override
                public void onError(String error, String tag) {
                    CommonClass.showSnackbarMessage(linear_rootElement,error);
                }
            });
        } else CommonClass.showSnackbarMessage(linear_rootElement,getResources().getString(R.string.NoInternetAccess));
    }

    /**
     * <h>PostMedia</h>
     * <p>
     * In this method we are uploading file(Image or video) to the cloudinary server.
     * in Respose we will get Url. Once we get Url of the image then call registration
     * api.
     * </p>
     *
     * @param remaining_data The bundle of cloudinary details data
     */
    private void postMedia(Bundle remaining_data) {
        /*
         * Creating the path holder.*/
        CloudData cloudData = new CloudData();
        cloudData.setPath(mFile.getAbsolutePath());
        cloudData.setVideo(false);

        new UploadToCloudinary(remaining_data, cloudData) {
            @Override
            public void callBack(Map resultData) {
                if (resultData != null) {
                    String main_url = (String) resultData.get("url");
                    System.out.println(TAG + " " + "main url=" + main_url);
                    profilePicUrl=main_url;
                    updateProfileApi();
                }
            }

            @Override
            public void errorCallBack(String error) {

            }
        };
    }

    /**
     * <h>UpdateProfileApi</h>
     * <p>
     *     In this method we used to send the modified datas to the server.
     * </p>
     */
    private void updateProfileApi()
    {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            JSONObject request_datas = new JSONObject();

            try {
                request_datas.put("token",mSessionManager.getAuthToken());
                request_datas.put("username",eT_userName.getText().toString());
                request_datas.put("fullName",eT_fullName.getText().toString());
                request_datas.put("bio",eT_bio.getText().toString());
                request_datas.put("website",eT_website.getText().toString());
                request_datas.put("gender",tV_gender.getText().toString());
                request_datas.put("profilePicUrl", profilePicUrl);
                request_datas.put("location",tV_location.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.SAVE_PROFILE, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag)
                {
                    System.out.println(TAG+" "+"update profile res="+result);

                    EditProfilePojo editProfilePojo;
                    Gson gson=new Gson();
                    editProfilePojo=gson.fromJson(result,EditProfilePojo.class);

                    switch (editProfilePojo.getCode())
                    {
                        // Success
                        case "200" :
                            // save user name and user profile pic into session manager class
                            mSessionManager.setUserImage(profilePicUrl);
                            mSessionManager.setUserName(eT_userName.getText().toString());

                            // set datas to notify the updated datas in profile screen
                            ProfileResultDatas profileResultDatas = new ProfileResultDatas();
                            profileResultDatas.setProfilePicUrl(profilePicUrl);
                            profileResultDatas.setUsername(eT_userName.getText().toString());
                            profileResultDatas.setFullName(eT_fullName.getText().toString());
                            profileResultDatas.setBio(eT_bio.getText().toString());
                            profileResultDatas.setWebsite(eT_website.getText().toString());
                            BusProvider.getInstance().post(profileResultDatas);
                            onBackPressed();

                            /*Intent intent=new Intent();
                            intent.putExtra("profilePicUrl",profilePicUrl);
                            intent.putExtra("username",eT_userName.getText().toString());
                            intent.putExtra("fullName",eT_fullName.getText().toString());
                            intent.putExtra("bio",eT_bio.getText().toString());
                            intent.putExtra("website",eT_website.getText().toString());
                            setResult(VariableConstants.PROFILE_REQUEST_CODE,intent);*/
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // Error
                        default:
                            rL_pBar.setVisibility(View.GONE);
                            tV_save.setVisibility(View.VISIBLE);
                            tV_save.setVisibility(View.VISIBLE);
                            CommonClass.showSnackbarMessage(linear_rootElement,editProfilePojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag)
                {
                    rL_pBar.setVisibility(View.GONE);
                    tV_save.setVisibility(View.VISIBLE);
                    CommonClass.showSnackbarMessage(linear_rootElement,error);
                }
            });
        }
        else CommonClass.showSnackbarMessage(linear_rootElement,getResources().getString(R.string.NoInternetAccess));
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

       try {
           InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
           if (imm!=null)
               imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
           return true;
       }catch (Exception e){

       }
       return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
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
                        chooseImage();
                    }
                }
        }
    }
}
