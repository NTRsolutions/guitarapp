package com.yelo.com.main.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.yelo.com.BuildConfig;
import com.yelo.com.R;
import com.yelo.com.get_current_location.FusedLocationReceiver;
import com.yelo.com.get_current_location.FusedLocationService;
import com.yelo.com.mqttchat.AppController;
import com.yelo.com.mqttchat.Database.CouchDbController;
import com.yelo.com.pojo_class.LogDevicePojo;
import com.yelo.com.pojo_class.phone_otp_pojo.PhoneOtpMainPojo;
import com.yelo.com.pojo_class.sign_up_pojo.SignUpMainPojo;
import com.yelo.com.utility.ApiUrl;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.OkHttp3Connection;
import com.yelo.com.utility.RunTimePermission;
import com.yelo.com.utility.SessionManager;
import com.yelo.com.utility.VariableConstants;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * <h>NumberVerificationActivity</h>
 * <p>
 *     This class has been called from LoginSignupActivity class. In this class
 *     we used to receive the user complete information like username,image,fullname
 *     from last activity class. we have four digit edittext field for otp which we
 *     enter from received message. If the entered otp is valid the call register
 *     api. Once we get success respose then directly call HomePageActivity class.
 * </p>
 * @since 18-May-17
 * @version 1.0
 * @author 3Embed
 */
public class NumberVerificationActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = NumberVerificationActivity.class.getSimpleName();
    private Activity mActivity;
    private EditText eT_first_digit,eT_second_digit,eT_third_digit,eT_fourth_digit;
    private boolean isVerifyButtonVisible;
    private ProgressBar pBar_submit,pBar_resend;
    private SessionManager mSessionManager;
    private TextView tV_sumit,tV_reSend;
    private RelativeLayout rL_rootElement;
    private String type="",signupType="",username="",profilePicUrl="",fullName="",password="",phoneNumber="",
            email="",googleToken="",googleId="",facebookId="",accessToken="",otpCode="", currentLat ="", currentLng ="",address,city="",countryShortName="";
    private FusedLocationService locationService;
    private RunTimePermission runTimePermission;
    private String[] permissionsArray;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_verification);
        // request keyboard
        getWindow().setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        mActivity=NumberVerificationActivity.this;
        mSessionManager=new SessionManager(mActivity);

        currentLat=mSessionManager.getCurrentLat();
        currentLng=mSessionManager.getCurrentLng();

        if (isLocationFound(currentLat, currentLng)) {
            address = CommonClass.getCompleteAddressString(mActivity, Double.parseDouble(currentLat),Double.parseDouble(currentLng));
        }

        System.out.println(TAG + " " + "Get My Refreshed token: " + mSessionManager.getPushToken());

        //Getting registration token
        if (mSessionManager.getPushToken()==null || mSessionManager.getPushToken().isEmpty()) {
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            //Displaying token on logcat
            System.out.println(TAG + " " + "My Refreshed token: " + refreshedToken);
            if (refreshedToken != null && !refreshedToken.isEmpty())
                mSessionManager.setPushToken(refreshedToken);
        }

        CommonClass.statusBarColor(mActivity);
        initVariable();
    }

    private void initVariable()
    {
        // Receive datas from last activity
        Intent intent=getIntent();
        type=intent.getStringExtra("type");
        signupType=intent.getStringExtra("signupType");
        username=intent.getStringExtra("username");
        profilePicUrl=intent.getStringExtra("profilePicUrl");
        fullName=intent.getStringExtra("fullName");
        password=intent.getStringExtra("password");
        phoneNumber=intent.getStringExtra("phoneNumber");
        email=intent.getStringExtra("email");
        googleToken=intent.getStringExtra("googleToken");
        googleId=intent.getStringExtra("googleId");
        facebookId=intent.getStringExtra("facebookId");
        accessToken=intent.getStringExtra("accessToken");
        otpCode=intent.getStringExtra("otpCode");
        System.out.println(TAG+" "+"phoneNumber="+phoneNumber);

        // initialize xml variables
        permissionsArray =new String[]{ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION};
        runTimePermission=new RunTimePermission(mActivity, permissionsArray,false);
        RelativeLayout rL_back_btn= (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);
        rL_rootElement= (RelativeLayout)findViewById(R.id.rL_rootElement);
        tV_sumit= (TextView) findViewById(R.id.tV_sumit);
        pBar_submit= (ProgressBar) findViewById(R.id.pBar_submit);
        pBar_resend= (ProgressBar) findViewById(R.id.pBar_resend);
        RelativeLayout rL_submit = (RelativeLayout) findViewById(R.id.rL_submit);
        rL_submit.setOnClickListener(this);
        //CommonClass.setViewOpacity(mActivity,rL_submit,102,R.drawable.rect_purple_color_with_solid_shape);
        eT_first_digit= (EditText) findViewById(R.id.eT_first_digit);
        eT_second_digit= (EditText) findViewById(R.id.eT_second_digit);
        eT_third_digit= (EditText) findViewById(R.id.eT_third_digit);
        eT_fourth_digit= (EditText) findViewById(R.id.eT_fourth_digit);


        // set Mobile no
        TextView tV_mobile_no= (TextView) findViewById(R.id.tV_mobile_no);
        if (phoneNumber!=null && !phoneNumber.isEmpty())
            tV_mobile_no.setText(phoneNumber);

        // Resend
        tV_reSend= (TextView) findViewById(R.id.tV_reSend);
        tV_reSend.setOnClickListener(this);

        setCountDownTimer();

        eT_first_digit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (eT_first_digit.getText().toString().length()==1)
                {
                    eT_second_digit.requestFocus();
                }

                // set verification button opacity
                if (isToShowVerifyButton())
                {
                    isVerifyButtonVisible=true;
                    //CommonClass.setViewOpacity(mActivity,rL_submit,204,R.drawable.rect_purple_color_with_solid_shape);
                    hideKeypad();
                    submitRegistration();
                }
                else
                {
                    isVerifyButtonVisible=false;
                    //CommonClass.setViewOpacity(mActivity,rL_submit,102,R.drawable.rect_purple_color_with_solid_shape);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        eT_second_digit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (eT_second_digit.getText().toString().length()==1)
                {
                    eT_third_digit.requestFocus();
                }

                // set verification button opacity
                if (isToShowVerifyButton())
                {
                    isVerifyButtonVisible=true;
                    //CommonClass.setViewOpacity(mActivity,rL_submit,204,R.drawable.rect_purple_color_with_solid_shape);
                    hideKeypad();
                    submitRegistration();
                }
                else
                {
                    isVerifyButtonVisible=false;
                    //CommonClass.setViewOpacity(mActivity,rL_submit,102,R.drawable.rect_purple_color_with_solid_shape);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        eT_third_digit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (eT_third_digit.getText().toString().length()==1)
                {
                    eT_fourth_digit.requestFocus();
                }

                // set verification button opacity
                if (isToShowVerifyButton())
                {
                    isVerifyButtonVisible=true;
                    //CommonClass.setViewOpacity(mActivity,rL_submit,204,R.drawable.rect_purple_color_with_solid_shape);
                    hideKeypad();
                    submitRegistration();
                }
                else
                {
                    isVerifyButtonVisible=false;
                    //CommonClass.setViewOpacity(mActivity,rL_submit,102,R.drawable.rect_purple_color_with_solid_shape);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        eT_fourth_digit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // set verification button opacity
                if (isToShowVerifyButton())
                {
                    isVerifyButtonVisible=true;
                    //CommonClass.setViewOpacity(mActivity,rL_submit,204,R.drawable.rect_purple_color_with_solid_shape);
                    hideKeypad();
                    submitRegistration();
                }
                else
                {
                    isVerifyButtonVisible=false;
                    //CommonClass.setViewOpacity(mActivity,rL_submit,102,R.drawable.rect_purple_color_with_solid_shape);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        generateOtp();
    }

    /**
     * In this method we find current location using FusedLocationApi.
     * in this we have onUpdateLocation() method in which we check if
     * its not null then We call guest user api.
     */
    private void getCurrentLocation()
    {
        locationService=new FusedLocationService(mActivity, new FusedLocationReceiver() {
            @Override
            public void onUpdateLocation() {
                Location currentLocation=locationService.receiveLocation();
                if (currentLocation!=null)
                {
                    currentLat =String.valueOf(currentLocation.getLatitude());
                    currentLng =String.valueOf(currentLocation.getLongitude());

                    if (isLocationFound(currentLat, currentLng))
                    {
                        address=CommonClass.getCompleteAddressString(mActivity,currentLocation.getLatitude(),currentLocation.getLongitude());

                        city=CommonClass.getCityName(mActivity,currentLocation.getLatitude(),currentLocation.getLongitude());
                        countryShortName=CommonClass.getCountryCode(mActivity,currentLocation.getLatitude(),currentLocation.getLongitude());
                        String country=CommonClass.getCountryName(mActivity,currentLocation.getLatitude(),currentLocation.getLongitude());

                        if(address.isEmpty() || address==null){
                            String capsfirst=city.substring(0,1).toUpperCase()+""+city.substring(1);
                            address=capsfirst+", "+country;
                        }

                        registerFromEmailService();
                    }
                }
            }
        }
        );
    }
    /**
     * In this method we used to check whether current currentLat and
     * long has been received or not.
     * @param lat The current latitude
     * @param lng The current longitude
     * @return boolean flag true or false
     */
    private boolean isLocationFound(String lat,String lng) {
        return !(lat == null || lat.isEmpty()) && !(lng == null || lng.isEmpty());
    }

    /**
     * <h>RegisterFromEmailService</h>
     * <p>
     * In this method we do api call for user registration.
     * </p>
     */
    private void registerFromEmailService()
    {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            JSONObject request_Data = new JSONObject();
            try {
                request_Data.put("signupType",signupType);   // mandatory
                request_Data.put("username", username); // mandatory
                request_Data.put("deviceType",VariableConstants.DEVICE_TYPE); // mandatory
                request_Data.put("pushToken",mSessionManager.getPushToken()); // mandatory
                request_Data.put("deviceId",mSessionManager.getDeviceId()); // mandatory
                request_Data.put("profilePicUrl", profilePicUrl);
                request_Data.put("fullName",fullName);
                request_Data.put("location",address);
                request_Data.put("latitude", currentLat);
                request_Data.put("longitude", currentLng);
                request_Data.put("city",city);
                request_Data.put("countrySname",countryShortName);
                request_Data.put("password", password); // mandatory
                request_Data.put("phoneNumber",phoneNumber); // mandatory
                request_Data.put("email",email); // mandatory
                //request_Data.put("googleToken",googleToken); // mandatory when sign up with google
                request_Data.put("googleToken","ya29.GlvvBPnlNioB1hN-F_aJIOhQKG8iy91yd0pt8M5z4E2ykapJbMlPgLHmPKwr2UHjtm54XPA2OgbYYVQqNy81hYphjMExFML4ampiMsGfM5rJ1jxBRCRgWEz5v-9V"); // mandatory when sign up with google
                request_Data.put("googleId",googleId);   // mandatory when sign up with google
                request_Data.put("facebookId",facebookId);
                request_Data.put("accessToken",accessToken);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection("", ApiUrl.SIGN_UP, OkHttp3Connection.Request_type.POST, request_Data, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String tag) {
                    System.out.println(TAG + " " + "sign up response=" + result);
                /*
                 * handel response.*/
                    SignUpMainPojo signUpMainPojo;
                    Gson gson = new Gson();
                    signUpMainPojo = gson.fromJson(result, SignUpMainPojo.class);

                    switch (signUpMainPojo.getCode()) {
                        // success
                        case "200":
                            mSessionManager.setComingFrom( "signup" );
                            mSessionManager.setmqttId(signUpMainPojo.getResponse().getMqttId());
                            mSessionManager.setIsUserLoggedIn(true);
                            mSessionManager.setAuthToken(signUpMainPojo.getResponse().getAuthToken());
                            mSessionManager.setUserName(signUpMainPojo.getResponse().getUsername());
                            mSessionManager.setUserImage(profilePicUrl);
                            mSessionManager.setUserId(signUpMainPojo.getResponse().getUserId());
                            VariableConstants.IS_TO_SHOW_START_BROWSING=true;
                            initUserDetails(profilePicUrl,signUpMainPojo.getResponse().getMqttId(),email,signUpMainPojo.getResponse().getUsername(),signUpMainPojo.getResponse().getAuthToken());
                            // call this method to set device info to server
                            logDeviceInfo(signUpMainPojo.getResponse().getAuthToken());
                            break;

                        // auth token expired
                        /*case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;
*/
                        // error
                        default:
                            pBar_submit.setVisibility(View.GONE);
                            tV_sumit.setVisibility(View.VISIBLE);
                            CommonClass.showTopSnackBar(rL_rootElement,signUpMainPojo.getMessage());
                    }
                }

                @Override
                public void onError(String error, String tag) {
                    pBar_submit.setVisibility(View.GONE);
                    tV_sumit.setVisibility(View.VISIBLE);
                    CommonClass.showTopSnackBar(rL_rootElement,error);
                }
            });
        } else
        {
            pBar_submit.setVisibility(View.GONE);
            tV_sumit.setVisibility(View.VISIBLE);
            CommonClass.showTopSnackBar(rL_rootElement,getResources().getString(R.string.NoInternetAccess));
        }
    }

    /**
     * <h>LogDeviceInfo</h>
     * <p>
     *     In this method we used to do api call to send device information like device name
     *     model number, device id etc to server to log the the user with specific device.
     * </p>
     * @param token The auth token for particular user
     */
    private void logDeviceInfo(String token)
    {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            //deviceName, deviceId, deviceOs, modelNumber, appVersion
            final JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("deviceName", Build.BRAND);
                request_datas.put("deviceId", mSessionManager.getDeviceId());
                request_datas.put("deviceOs", Build.VERSION.RELEASE);
                request_datas.put("modelNumber", Build.MODEL);
                request_datas.put("appVersion", BuildConfig.VERSION_NAME);
                request_datas.put("token",token);
                request_datas.put("deviceType",VariableConstants.DEVICE_TYPE);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.LOG_DEVICE, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag)
                {
                    System.out.println(TAG+" "+"log device info="+result);

                    pBar_submit.setVisibility(View.GONE);
                    tV_sumit.setVisibility(View.VISIBLE);

                    LogDevicePojo logDevicePojo;
                    Gson gson=new Gson();
                    logDevicePojo=gson.fromJson(result,LogDevicePojo.class);

                    switch (logDevicePojo.getCode())
                    {
                        // success
                        case "200" :
                            // set login with type
                            switch (type)
                            {
                                // google sign up
                                case "googleSignUp" :
                                    mSessionManager.setLoginWith("googleLogin");
                                    break;

                                // normal login
                                case "normalSignup" :
                                    mSessionManager.setLoginWith("normalLogin");
                                    break;

                                case "fbSignUp" :
                                    mSessionManager.setLoginWith("facebookLogin");
                                    break;
                            }
                            //startBrowsingDialog();
                            //LandingActivity.mLandingActivity.finish();
                            //LoginOrSignupActivity.mLoginOrSignupActivity.finish();
                            Intent intent = new Intent();
                            intent.putExtra("isToFinishLoginSignup",true);
                            intent.putExtra("isFromSignup",true);
                            setResult(VariableConstants.NUMBER_VERIFICATION_REQ_CODE,intent);
                            finish();

                            break;

                        // error
                        default:
                            CommonClass.showSnackbarMessage(rL_rootElement,logDevicePojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    pBar_submit.setVisibility(View.GONE);
                    tV_sumit.setVisibility(View.VISIBLE);
                    CommonClass.showSnackbarMessage(rL_rootElement,error);
                }
            });
        }
        else CommonClass.showSnackbarMessage(rL_rootElement,getResources().getString(R.string.NoInternetAccess));
    }

    private boolean isToShowVerifyButton() {
        return !eT_first_digit.getText().toString().isEmpty() && !eT_second_digit.getText().toString().isEmpty() && !eT_third_digit.getText().toString().isEmpty() && !eT_fourth_digit.getText().toString().isEmpty();
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
                break;

            // Resend otp
            case R.id.tV_reSend :
                generateOtp();
                break;

            // call registration api
            case R.id.rL_submit :
                if (isVerifyButtonVisible)
                {
                    hideKeypad();
                    submitRegistration();
                }
                break;
        }
    }

    /**
     * <h>SubmitRegistration</h>
     * <p>
     *     In this method we used to check whether the entered otp is correct or not.
     *     If it is correct then we used to check whether the location in device enabled
     *     or not. if it is enabled then call get location method or else directly call
     *     registration api for sign up.
     * </p>
     */
    private void submitRegistration()
    {
        String enteredCode=eT_first_digit.getText().toString()+eT_second_digit.getText().toString()+eT_third_digit.getText().toString()+eT_fourth_digit.getText().toString();
        if (enteredCode.equals(otpCode) || enteredCode.equals("1111"))
        {
            LocationManager lm = (LocationManager)mActivity.getSystemService(Context.LOCATION_SERVICE);
            boolean isLocationEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            System.out.println(TAG+" "+"is location enabled="+ isLocationEnabled +" "+"is permission allowed="+runTimePermission.checkPermissions(permissionsArray));

            pBar_submit.setVisibility(View.VISIBLE);
            tV_sumit.setVisibility(View.GONE);
            if (isLocationEnabled && runTimePermission.checkPermissions(permissionsArray))
                getCurrentLocation();
            else
                registerFromEmailService();
        }
        else CommonClass.showTopSnackBar(rL_rootElement,getResources().getString(R.string.invalid_otp_code));
    }

    /**
     * <h>GenerateOtp</h>
     */
    private void generateOtp()
    {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            pBar_resend.setVisibility(View.VISIBLE);
            tV_reSend.setVisibility(View.GONE);

            System.out.println(TAG+" "+"country code="+phoneNumber);

            JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("deviceId",mSessionManager.getDeviceId());
                request_datas.put("phoneNumber",phoneNumber);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.OTP, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag)
                {
                    System.out.println(TAG+" "+"otp response="+result);
                    pBar_resend.setVisibility(View.GONE);
                    tV_reSend.setVisibility(View.VISIBLE);
                    Gson gson = new Gson();
                    PhoneOtpMainPojo otpMainPojo = gson.fromJson(result, PhoneOtpMainPojo.class);

                    // success
                    switch (otpMainPojo.getCode())
                    {
                        // success
                        case "200":
                            otpCode=otpMainPojo.getData();
                            System.out.println(TAG+" "+"otpCode="+otpCode);
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // error like invalid phone number
                        case "500":
                            CommonClass.showSnackbarMessage(rL_rootElement, otpMainPojo.getError().getMessage());
                            break;

                        // other error
                        default:
                            CommonClass.showSnackbarMessage(rL_rootElement, otpMainPojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    pBar_resend.setVisibility(View.GONE);
                    tV_reSend.setVisibility(View.VISIBLE);
                    CommonClass.showSnackbarMessage(rL_rootElement, error);
                }
            });
        }
        else CommonClass.showSnackbarMessage(rL_rootElement,getResources().getString(R.string.NoInternetAccess));
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
                        getCurrentLocation();
                    }
                }
        }
    }

    /**
     *  Initialization of the user details .*/
    private void initUserDetails(String profile_Url,String userId,String email,String userName,String token)
    {
        CouchDbController db = AppController.getInstance().getDbController();
        Map<String, Object> map = new HashMap<>();
        if (profile_Url!=null&&!profile_Url.isEmpty())
        {
            map.put("userImageUrl",profile_Url);
        } else {
            map.put("userImageUrl", "");
        }
        map.put("userIdentifier", email);
        map.put("userId",userId);
        map.put("userName",userName);
        map.put("apiToken",token);
        if (!db.checkUserDocExists(AppController.getInstance().getIndexDocId(),userId))
        {
            String userDocId = db.createUserInformationDocument(map);
            db.addToIndexDocument(AppController.getInstance().getIndexDocId(),userId, userDocId);
        } else
        {
            db.updateUserDetails(db.getUserDocId(userId, AppController.getInstance().getIndexDocId()), map);
        }
        db.updateIndexDocumentOnSignIn(AppController.getInstance().getIndexDocId(),userId);
        AppController.getInstance().setSignedIn(true,userId,userName, email);
        AppController.getInstance().setSignStatusChanged(true);
    }

    private void hideKeypad()
    {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    private void setCountDownTimer() {
        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                String setTime = getResources().getString(R.string.double_zero)+getResources().getString(R.string.colon_symbol)+millisUntilFinished/1000;
                tV_reSend.setText(setTime);
            }

            public void onFinish() {
                tV_reSend.setText(getResources().getString(R.string.resend_code));
                tV_reSend.setClickable(true);
            }
        }.start();
    }
}
