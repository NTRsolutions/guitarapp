package com.yelo.com.main.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.yelo.com.BuildConfig;
import com.yelo.com.R;
import com.yelo.com.county_code_picker.Country;
import com.yelo.com.county_code_picker.DialogCountryList;
import com.yelo.com.county_code_picker.SetCountryCodeListener;
import com.yelo.com.device_camera.HandleCameraEvents;
import com.yelo.com.get_current_location.FusedLocationReceiver;
import com.yelo.com.get_current_location.FusedLocationService;
import com.yelo.com.mqttchat.AppController;
import com.yelo.com.mqttchat.Database.CouchDbController;
import com.yelo.com.pojo_class.CloudData;
import com.yelo.com.pojo_class.EmailCheckPojo;
import com.yelo.com.pojo_class.LogDevicePojo;
import com.yelo.com.pojo_class.LoginResponsePojo;
import com.yelo.com.pojo_class.PhoneNoCheckPojo;
import com.yelo.com.pojo_class.UserNameCheckPojo;
import com.yelo.com.pojo_class.cloudinary_details_pojo.Cloudinary_Details_reponse;
import com.yelo.com.pojo_class.phone_otp_pojo.PhoneOtpMainPojo;
import com.yelo.com.utility.ApiUrl;
import com.yelo.com.utility.CircleTransform;
import com.yelo.com.utility.CommonClass;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import co.simplecrop.android.simplecropimage.CropImage;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.yelo.com.utility.VariableConstants.TEMP_PHOTO_FILE_NAME;

/**
 * <h>LoginOrSignupActivity</h>
 * <p>
 *     This activity class has been called from LandingActivity class.
 *     In this class two button(Login and signup) is there on the top
 *     of the screen.
 * </p>
 * @since 15-May-17
 */
public class LoginOrSignupActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = LoginOrSignupActivity.class.getSimpleName();
    private Activity mActivity;
    private View login_views,signup_views;
    private RunTimePermission runTimePermission;
    private String[] permissionsArray;
    private SessionManager mSessionManager;
    private RelativeLayout rL_rootElement;
    private boolean isPictureTaken,isFromLocation;
    private FusedLocationService locationService;
    private String signUpType="",googleToken="",googleId="",fbId,type="",google_userImageUrl="",fb_userImageUrl="",
            fb_accessToken="", currentLat ="", currentLng ="",address="",otpCode="",city="",countryShortName="";
    private boolean isUserRegistered,isPhoneRegistered,isEmailRegistered,isToStartActivity;

    // Login xml variables
    private EditText eT_loginUserName, eT_loginPassword;
    private RelativeLayout rL_do_login;
    private RelativeLayout rL_signup;
    private ImageView iV_login_userName_error,iV_login_password_error;

    // signup xml variables
    private EditText eT_userName,eT_fullName,eT_password,eT_mobileNo,eT_emailId;
    private boolean isLoginButtonEnabled,isSignUpButtonEnabled,isAskLocationPermission;
    private ProgressBar progress_bar_login,progress_bar_signup,progress_bar_ph,pBar_userName,pBar_email;
    private TextView tV_do_login,tV_signup,tV_by_signing_up;
    private ImageView iV_profile_pic,iV_userName_error,iV_password_error,iV_error_ph,iV_error_email;
    private ArrayList<Country> arrayListCountry;
    private DialogCountryList dialogCountryList;
    private TextView tV_country_iso_no,tV_country_code;
    private ImageView iV_full_name,iV_user_name,iV_password,iV_phone_icon,iV_email,iv_edit_icon;
    private CheckBox checkboxSignUp;
    private String countryIsoNumber="";
    private File mFile;
    private HandleCameraEvents mHandleCameraEvents;

    public  static boolean is_market = false;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

        mActivity=LoginOrSignupActivity.this;
        isToStartActivity = true;
        mSessionManager=new SessionManager(mActivity);



        permissionsArray =new String[]{ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION};
        runTimePermission=new RunTimePermission(mActivity, permissionsArray,false);

        if(!runTimePermission.checkPermissions(permissionsArray)){
            isAskLocationPermission=true;
            runTimePermission.requestPermission();
        }

        currentLat=mSessionManager.getCurrentLat();
        currentLng=mSessionManager.getCurrentLng();
        if (isLocationFound(currentLat, currentLng)) {
            address = CommonClass.getCompleteAddressString(mActivity, Double.parseDouble(currentLat),Double.parseDouble(currentLng));
            city = CommonClass.getCityName(mActivity, Double.parseDouble(currentLat),Double.parseDouble(currentLng));
            countryShortName=CommonClass.getCountryCode(mActivity,Double.parseDouble(currentLat),Double.parseDouble(currentLng));
        }

        // hide status bar
        CommonClass.statusBarColor(mActivity);
        initVariables();
    }

    private void initVariables()
    {
        isPictureTaken=false;
        arrayListCountry = new ArrayList<>();
        rL_rootElement= (RelativeLayout) findViewById(R.id.rL_rootElement);
        login_views=findViewById(R.id.login_views);
        signup_views=findViewById(R.id.signup_views);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        RadioGroup radioGroupOptions = (RadioGroup) findViewById(R.id.radioGroupOptions);

        RadioButton radio_login,radio_signup, radio_consumer, radio_market_store;
        radio_login= (RadioButton) findViewById(R.id.radio_login);
        radio_signup= (RadioButton) findViewById(R.id.radio_signup);



        // receiving flag value from last class
        Intent intent=getIntent();
        type=intent.getStringExtra("type");

        System.out.println(TAG+" "+"type="+type);

        // initialize sigup xml variables
        eT_userName = (EditText)signup_views.findViewById(R.id.eT_userName);
        eT_fullName = (EditText)signup_views.findViewById(R.id.eT_fullName);
        eT_fullName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        eT_emailId = (EditText)signup_views.findViewById(R.id.eT_emailId);
        iV_profile_pic= (ImageView) signup_views.findViewById(R.id.iV_profile_pic);
        iV_profile_pic.getLayoutParams().width=CommonClass.getDeviceWidth(mActivity)/4;
        iV_profile_pic.getLayoutParams().height=CommonClass.getDeviceWidth(mActivity)/4;
        iV_profile_pic.setOnClickListener(this);
        progress_bar_ph= (ProgressBar) signup_views.findViewById(R.id.pBar_ph);
        progress_bar_ph.setVisibility(View.GONE);
        pBar_userName= (ProgressBar) findViewById(R.id.pBar_userName);
        pBar_userName.setVisibility(View.GONE);
        pBar_email= (ProgressBar) findViewById(R.id.pBar_email);
        pBar_email.setVisibility(View.GONE);

        // sign up icons
        radio_consumer= findViewById(R.id.radio_consumer);
        radio_market_store= findViewById(R.id.radio_market);

        iV_full_name= (ImageView) signup_views.findViewById(R.id.iV_full_name);
        iV_user_name= (ImageView) signup_views.findViewById(R.id.iV_user_name);
        iV_password= (ImageView) signup_views.findViewById(R.id.iV_password);
        iV_phone_icon= (ImageView) signup_views.findViewById(R.id.iV_phone_icon);
        iV_email= (ImageView) signup_views.findViewById(R.id.iV_email);
        iv_edit_icon= (ImageView) signup_views.findViewById(R.id.iv_edit_icon);

        iv_edit_icon.setVisibility(View.GONE);

        // sign up error icon
        iV_userName_error= (ImageView) signup_views.findViewById(R.id.iV_userName_error);
        iV_password_error= (ImageView) signup_views.findViewById(R.id.iV_password_error);
        iV_error_ph= (ImageView) signup_views.findViewById(R.id.iV_error_ph);
        iV_error_email= (ImageView) signup_views.findViewById(R.id.iV_error_email);

        // Here we check the type if it is login type then show login views else sign up
        switch (type) {
            case "login":
                radio_login.setChecked(true);
                radio_signup.setChecked(false);
                login_views.setVisibility(View.VISIBLE);
                signup_views.setVisibility(View.GONE);
                break;

            // normal signup
            case "normalSignup":
                radio_login.setChecked(false);
                radio_signup.setChecked(true);
                login_views.setVisibility(View.GONE);
                signup_views.setVisibility(View.VISIBLE);
                break;

            // google sign up
            case "googleSignUp":
                radio_login.setChecked(false);
                radio_signup.setChecked(true);
                login_views.setVisibility(View.GONE);
                signup_views.setVisibility(View.VISIBLE);

                // Google login
                String userFullName,email;
                userFullName = intent.getStringExtra("userFullName");
                google_userImageUrl = intent.getStringExtra("userImageUrl");
                email = intent.getStringExtra("email");
                googleId = intent.getStringExtra("id");
                googleToken = intent.getStringExtra("serverAuthCode");

                // set profile pic
                setSignUpXmlVar(google_userImageUrl,userFullName,email);

                signUpType="3";
                break;

            case "fbSignUp" :
                radio_login.setChecked(false);
                radio_signup.setChecked(true);
                login_views.setVisibility(View.GONE);
                signup_views.setVisibility(View.VISIBLE);

                // facebook login
                String fbUserFullName,fbEmail, fbUserName;
                fbUserFullName = intent.getStringExtra("userFullName");
                fb_userImageUrl = intent.getStringExtra("userImageUrl");
                fbEmail = intent.getStringExtra("email");
                fbId = intent.getStringExtra("id");
                fbUserName = intent.getStringExtra("userName");
                fb_accessToken = intent.getStringExtra("fb_accessToken");

                // set values
                setSignUpXmlVar(fb_userImageUrl,fbUserFullName,fbEmail);

                signUpType="1";

                Log.e(TAG, "Facebook Name: " + fbUserFullName + ", email: " + fbEmail
                        + ", Image: " + fb_userImageUrl + ", fb id: " + fbId + ", user name: " + fbUserName );

                break;
        }

        // Here we set login or sign up views visibility
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId)
                {
                    // open login view
                    case R.id.radio_login :
                        login_views.setVisibility(View.VISIBLE);
                        signup_views.setVisibility(View.GONE);
                        break;

                    // open sign up view
                    case R.id.radio_signup :
                        if (type.equals("login"))
                            type="normalSignup";
                        login_views.setVisibility(View.GONE);
                        signup_views.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        // Here we set login or sign up views visibility
        radioGroupOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId)
                {
                    // open login view
                    case R.id.radio_consumer :
                        radio_consumer.setChecked(true);
                        radio_market_store.setChecked(false);

                        is_market = true;

                        break;

                    // open sign up view
                    case R.id.radio_market :
                        radio_consumer.setChecked(false);
                        radio_market_store.setChecked(true);
                        is_market = false;
                        break;
                }
            }
        });


        // Back button
        RelativeLayout rL_back_btn = (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);

        // Login button
        iV_login_userName_error= (ImageView) login_views.findViewById(R.id.iV_login_userName_error);
        iV_login_password_error= (ImageView) login_views.findViewById(R.id.iV_login_password_error);
        rL_do_login= (RelativeLayout) login_views.findViewById(R.id.rL_do_login);
        rL_do_login.setOnClickListener(this);

        // login progress bar
        progress_bar_login= (ProgressBar) login_views.findViewById(R.id.progress_bar_login);
        tV_do_login= (TextView) login_views.findViewById(R.id.tV_do_login);

        // Forgot password
        RelativeLayout rL_forgot_password = (RelativeLayout) findViewById(R.id.rL_forgot_password);
        rL_forgot_password.setOnClickListener(this);

        loginButtonValidation();

        //////////////// sign up ////////////////////
        tV_country_iso_no= (TextView) signup_views.findViewById(R.id.tV_country_iso_no);
        tV_country_code= (TextView) signup_views.findViewById(R.id.tV_country_code);
        progress_bar_signup= (ProgressBar)signup_views.findViewById(R.id.progress_bar_signup);
        progress_bar_signup.setVisibility(View.GONE);
        rL_signup= (RelativeLayout)signup_views.findViewById(R.id.rL_signup);
        tV_signup= (TextView)signup_views.findViewById(R.id.tV_signup);
        tV_by_signing_up= (TextView) signup_views.findViewById(R.id.tV_by_signing_up);
        checkboxSignUp= (CheckBox) signup_views.findViewById(R.id.checkboxSignUp);

        RelativeLayout rL_createAcc= (RelativeLayout) signup_views.findViewById(R.id.rL_createAcc);
        rL_createAcc.setOnClickListener(this);

        signUpButtonValidation();

        // call method to get all country iso code
        getCountryCodeList();

        RelativeLayout rL_country_picker = (RelativeLayout)signup_views.findViewById(R.id.rL_country_picker);
        rL_country_picker.setOnClickListener(this);

        setTermsNconditions();

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state))
        {
            mFile = new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME);
        }
        else {
            mFile = new File(getFilesDir(), TEMP_PHOTO_FILE_NAME);
        }
        mHandleCameraEvents=new HandleCameraEvents(mActivity,mFile);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isToStartActivity = true;
    }

    /**
     * <h>SetSignUpXmlVar</h>
     * <p>
     *     In this method we used to set the xml datas which we get
     *     from the last activity like LandingACtivity class.
     * </p>
     * @param imageUrl the profile image url
     * @param fullName The user full name
     * @param emailId The user eamil-Id
     */
    private void setSignUpXmlVar(String imageUrl,String fullName,String emailId)
    {
        // set profile pic
        if (imageUrl!=null && !imageUrl.isEmpty())
        {
            System.out.println(TAG+" "+"fb_userImageUrl="+fb_userImageUrl);
            Picasso.with(mActivity)
                    .load(imageUrl)
                    .transform(new CircleTransform())
                    .placeholder(R.drawable.add_photo)
                    .error(R.drawable.add_photo)
                    .into(iV_profile_pic);
        }

        // set full name
        if (fullName!=null && !fullName.isEmpty())
        {
            iV_full_name.setImageResource(R.drawable.name_on);
            eT_fullName.setText(fullName);
            eT_userName.requestFocus();
        }

        // email
        if (emailId!=null && !emailId.isEmpty())
        {
            iV_email.setImageResource(R.drawable.email_on);
            eT_emailId.setText(emailId);
            // email address validation api
            emailCheckApi();
        }
    }

    /**
     * <h>SetTermsNconditions</h>
     * <p>
     *     In this method we used to set the terms and condition & privacy policy text at
     *     bottom of the screen along with checkbox.
     * </p>
     */
    private void setTermsNconditions()
    {
        String terms,privacy;
        String s1=getResources().getString(R.string.by_signing_up);
        String s2=getResources().getString(R.string.termscondition);
        String s3=getResources().getString(R.string.and);
        String s4=getResources().getString(R.string.privacyPolicy);
        terms=getResources().getString(R.string.termsNconditionsUrl);
        privacy=getResources().getString(R.string.privacyPolicyUrl);

        String message=s1+" "+s2+" "+s3+" "+s4;
        SpannableString spannableString=new SpannableString(message);
        spannableString.setSpan(new MyClickableSpan(true,terms),s1.length()+1,s1.length()+s2.length()+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new MyClickableSpan(true,privacy),s1.length()+1+s2.length()+1+s3.length()+1,s1.length()+1+s2.length()+1+s3.length()+1+s4.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tV_by_signing_up.setText(spannableString);
        tV_by_signing_up.setMovementMethod(LinkMovementMethod.getInstance());
        tV_by_signing_up.setHighlightColor(Color.WHITE);
    }

    /**
     * <h>MyClickableSpan</h>
     * <p>
     *     In this method we used to set the text clickable part by part seperately
     *     like "Terms and Conditions" seperate and "Privacy Policy" seperate.
     * </p>
     */
    private class MyClickableSpan extends ClickableSpan
    {
        private boolean isUnderLine;
        private String setUrl;

        MyClickableSpan(boolean isUnderLine, String setUrl) {
            this.isUnderLine = isUnderLine;
            this.setUrl = setUrl;
        }

        @Override
        public void onClick(View widget)
        {
            Intent termsNconIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(setUrl));
            startActivity(termsNconIntent);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(isUnderLine);
            ds.setColor(ContextCompat.getColor(mActivity,R.color.white));
            ds.setFakeBoldText(false);
        }
    }

    /**
     * <h>loginButtonValidation</h>
     * <p>
     *     In this method we used to show login button more visible and clickable
     *     when all the mandatory fields are filled.
     * </p>
     */
    private void loginButtonValidation()
    {
        // set Opacity to login button
//        CommonClass.setViewOpacity(mActivity,rL_do_login,102,R.drawable.rect_purple_color_with_solid_shape);
        CommonClass.setViewOpacity(mActivity,rL_do_login,102,R.drawable.button_rounded_corners);

        // user name validation
        eT_loginUserName = (EditText)login_views.findViewById(R.id.eT_loginUserName);
        eT_loginUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //..user enter spcce then remove
                if(eT_loginUserName.getText().toString().contains(" ")){
                    eT_loginUserName.setText(eT_loginUserName.getText().toString().replace(" ",""));
                    eT_loginUserName.setSelection(eT_loginUserName.getText().toString().length());
                }

                if (isLoginParamFilled())
                {
                    isLoginButtonEnabled=true;
                    // set Opacity to login button
//                    CommonClass.setViewOpacity(mActivity,rL_do_login,204,R.drawable.rect_purple_color_with_solid_shape);
                    CommonClass.setViewOpacity(mActivity,rL_do_login,204,R.drawable.button_rounded_corners);
                }
                else
                {
                    isLoginButtonEnabled=false;
                    // set Opacity to login button
//                    CommonClass.setViewOpacity(mActivity,rL_do_login,100,R.drawable.rect_purple_color_with_solid_shape);
                    CommonClass.setViewOpacity(mActivity,rL_do_login,204,R.drawable.button_rounded_corners);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Password
        eT_loginPassword = (EditText) findViewById(R.id.eT_loginPassword);
        eT_loginPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isLoginParamFilled())
                {
                    isLoginButtonEnabled=true;
                    CommonClass.setViewOpacity(mActivity,rL_do_login,204,R.drawable.button_rounded_corners);
                }
                else
                {
                    isLoginButtonEnabled=false;
                    CommonClass.setViewOpacity(mActivity,rL_do_login,100,R.drawable.button_rounded_corners);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Login Edit text next click event
        eT_loginUserName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId==EditorInfo.IME_ACTION_NEXT)
                {
                    if (eT_loginUserName.getText().toString().isEmpty())
                    {
                        iV_login_userName_error.setVisibility(View.VISIBLE);
                        iV_login_userName_error.setImageResource(R.drawable.error_icon);
                        return true;
                    }
                    else
                    {
                        iV_login_userName_error.setVisibility(View.VISIBLE);
                        iV_login_userName_error.setImageResource(R.drawable.rightusername);
                        return false;
                    }
                }
                return false;
            }
        });

        // password
        eT_loginPassword.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    if (eT_loginPassword.getText().toString().isEmpty())
                    {
                        iV_login_password_error.setVisibility(View.VISIBLE);
                        iV_login_password_error.setImageResource(R.drawable.error_icon);
                    }
                    else
                    {
                        iV_login_password_error.setVisibility(View.VISIBLE);
                        iV_login_password_error.setImageResource(R.drawable.rightusername);
                    }

                    // if all the mandatory are filled then do login api call
                    if (isLoginButtonEnabled)
                    {
                        showKeyboard(InputMethodManager.HIDE_IMPLICIT_ONLY);

                        isFromLocation=true;
                        permissionsArray =new String[]{ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION};
                        runTimePermission=new RunTimePermission(mActivity, permissionsArray,false);

                        LocationManager lm = (LocationManager)mActivity.getSystemService(Context.LOCATION_SERVICE);
                        boolean isLocationEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                        System.out.println(TAG+" "+"is location enabled="+ isLocationEnabled +" "+"is permission allowed="+runTimePermission.checkPermissions(permissionsArray));

                        progress_bar_login.setVisibility(View.VISIBLE);
                        tV_do_login.setVisibility(View.GONE);
                        if (isLocationEnabled && runTimePermission.checkPermissions(permissionsArray))
                            getCurrentLocation();
                        else
                            loginRequestApi();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * <h>loginButtonValidation</h>
     * <p>
     *     In this method we used to show sign up button more visible and clickable
     *     when all the mandatory fields are filled.
     * </p>
     */
    private void signUpButtonValidation()
    {
        // set Opacity to login button
        CommonClass.setViewOpacity(mActivity,rL_signup,102,R.drawable.rect_purple_color_with_solid_shape);
        // user name
        eT_userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println(TAG + " " + "isSignUpButtonEnabled=" + isSignUpButtonEnabled);

                //..user enter spcce then remove

                if(eT_userName.getText().toString().contains(" ")){
                    eT_userName.setText(eT_userName.getText().toString().replace(" ",""));
                    eT_userName.setSelection(eT_userName.getText().toString().length());
                }

                // call this method to verify user name
                userNameCheckApi();

                if (isSignUpParamFilled()) {
                    isSignUpButtonEnabled = true;
                    // set Opacity to login button
                    CommonClass.setViewOpacity(mActivity,rL_signup,204,R.drawable.rect_purple_color_with_solid_shape);
                } else {
                    isSignUpButtonEnabled = false;
                    // set Opacity to login button
                    CommonClass.setViewOpacity(mActivity,rL_signup,102,R.drawable.rect_purple_color_with_solid_shape);
                }

                // change user icon
                String userName=eT_userName.getText().toString();
                if (userName.isEmpty())
                    iV_user_name.setImageResource(R.drawable.username_off);
                else iV_user_name.setImageResource(R.drawable.username_on);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // User Full name
        eT_fullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // change user name icon
                String fullName=eT_fullName.getText().toString();
                if (fullName.isEmpty())
                    iV_full_name.setImageResource(R.drawable.name_off);
                else iV_full_name.setImageResource(R.drawable.name_on);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Password
        eT_password = (EditText)signup_views.findViewById(R.id.eT_password);
        eT_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println(TAG + " " + "isSignUpButtonEnabled=" + isSignUpButtonEnabled);
                if (isSignUpParamFilled()) {
                    isSignUpButtonEnabled = true;
                    // set Opacity to login button
                    CommonClass.setViewOpacity(mActivity,rL_signup,204,R.drawable.rect_purple_color_with_solid_shape);
                } else {
                    isSignUpButtonEnabled = false;
                    // set Opacity to login button
                    CommonClass.setViewOpacity(mActivity,rL_signup,102,R.drawable.rect_purple_color_with_solid_shape);
                }

                // change user password icon
                String password=eT_password.getText().toString();
                if (password.isEmpty())
                    iV_password.setImageResource(R.drawable.password_off);
                else iV_password.setImageResource(R.drawable.password_on);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Mobile number
        eT_mobileNo = (EditText)signup_views.findViewById(R.id.eT_mobileNo);
        eT_mobileNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println(TAG + " " + "isSignUpButtonEnabled=" + isSignUpButtonEnabled);

                // call this method to verify mobile number
                phoneNumberCheckApi();

                if (isSignUpParamFilled()) {
                    isSignUpButtonEnabled = true;
                    // set Opacity to login button
                    CommonClass.setViewOpacity(mActivity,rL_signup,204,R.drawable.rect_purple_color_with_solid_shape);
                } else {
                    isSignUpButtonEnabled = false;
                    // set Opacity to login button
                    CommonClass.setViewOpacity(mActivity,rL_signup,102,R.drawable.rect_purple_color_with_solid_shape);
                }

                // change user mobile icon
                String mobileNo=eT_mobileNo.getText().toString();
                if (mobileNo.isEmpty())
                    iV_phone_icon.setImageResource(R.drawable.mobileniumber_off);
                else iV_phone_icon.setImageResource(R.drawable.mobilenumber_on);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Email Id
        eT_emailId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println(TAG + " " + "isSignUpButtonEnabled=" + isSignUpButtonEnabled);

                // check mobile number to verify email address
                if (CommonClass.isValidEmail(eT_emailId.getText().toString()))
                emailCheckApi();

                if (isSignUpParamFilled()) {
                    isSignUpButtonEnabled = true;
                    // set Opacity to login button
                    CommonClass.setViewOpacity(mActivity,rL_signup,204,R.drawable.rect_purple_color_with_solid_shape);
                } else {
                    isSignUpButtonEnabled = false;
                    // set Opacity to login button
                    CommonClass.setViewOpacity(mActivity,rL_signup,102,R.drawable.rect_purple_color_with_solid_shape);
                }

                // change user mobile icon
                String emailId=eT_emailId.getText().toString();
                if (emailId.isEmpty())
                    iV_email.setImageResource(R.drawable.email_off);
                else iV_email.setImageResource(R.drawable.email_on);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // password
        eT_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId==EditorInfo.IME_ACTION_NEXT)
                {
                    if (eT_password.getText().toString().isEmpty())
                    {
                        iV_password_error.setVisibility(View.VISIBLE);
                        iV_password_error.setImageResource(R.drawable.error_icon);
                        CommonClass.showSnackbarMessage(rL_rootElement,getResources().getString(R.string.please_enter_password));
                        return true;
                    }
                    else
                    {
                        //iV_password_error.setVisibility(View.VISIBLE);
                        //iV_password_error.setImageResource(R.drawable.rightusername);
                        return false;
                    }
                }
                return false;
            }
        });

        // set password validation
        eT_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                {
                    if (!TextUtils.isEmpty(eT_password.getText()))
                    {
                        iV_password_error.setVisibility(View.GONE);
                        //iV_password_error.setImageResource(R.drawable.rightusername);
                    }
                    else
                    {
                        iV_password_error.setVisibility(View.VISIBLE);
                        iV_password_error.setImageResource(R.drawable.error_icon);
                    }
                }
            }
        });
    }

    /**
     * <h>GetCountryCodeList</h>
     * <p>
     *     In this method we used to get the all country iso code and number into
     *     list. And find the user current country iso code and number and set
     *     before the mobile number.
     * </p>
     */
    private void getCountryCodeList() {
        String[] country_array = getResources().getStringArray(R.array.countryCodes);
        if (country_array.length > 0) {
            for (String aCountry_array : country_array) {
                String[] getCountryList;
                getCountryList = aCountry_array.split(",");
                String countryCode, countryName;
                countryCode = getCountryList[0];
                countryName = getCountryList[1];
                Country country = new Country();
                country.setCode(countryCode.trim());
                country.setName(countryName.trim());
                arrayListCountry.add(country);
            }

            if (arrayListCountry.size() > 0) {
                dialogCountryList=new DialogCountryList(mActivity,arrayListCountry);
                String countryIsoCode = Locale.getDefault().getCountry();
                if (countryIsoCode != null && !countryIsoCode.isEmpty()) {
                    String countryIsoNo = setCurrentCountryCode(countryIsoCode);
                    countryIsoNumber=getResources().getString(R.string.plus) + countryIsoNo;
                    System.out.println(TAG+" "+"countryIsoNumber="+countryIsoNumber);
                    tV_country_iso_no.setText(countryIsoNumber);
                    tV_country_code.setText(countryIsoCode);
                }
            }
        }
    }

    /**
     * <h>SetCurrentCountryCode</h>
     * <p>
     *     In this method we used to find the country iso number by giving its
     *     iso code.
     * </p>
     * @param isoCode The iso code of the country
     * @return it returns the country iso number e.g +91
     */
    private String setCurrentCountryCode(String isoCode) {
        String countryCode = "";
        for (Country country : arrayListCountry)
        {
            System.out.println(TAG+" "+"isoCode="+isoCode+" "+"country.getName()="+country.getName());
            if (country.getName().equals(isoCode))
            {
                countryCode = country.getCode();
                return countryCode;
            }
        }
        return countryCode;
    }

    /**
     * <h>checkValidation</h>
     * <p>
     *     In this method we used to check the mandatory field whether
     *     it has been filled or not.
     * </p>
     */
    private boolean isLoginParamFilled() {
        boolean isValid;
        isValid = !eT_loginUserName.getText().toString().isEmpty() && !eT_loginPassword.getText().toString().isEmpty();
        return isValid;
    }

    /**
     * <h>checkValidation</h>
     * <p>
     * In this method we used to check the mandatory field whether
     * it has been filled or not else call registation api.
     * </p>
     */
    private boolean isSignUpParamFilled() {
        boolean isValid;
        isValid = !eT_userName.getText().toString().isEmpty() && !eT_password.getText().toString().isEmpty() && !eT_mobileNo.getText().toString().isEmpty() && !eT_emailId.getText().toString().isEmpty() && CommonClass.isValidEmail(eT_emailId.getText().toString());
        return isValid;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            // Back button
            case R.id.rL_back_btn :
                onBackPressed();
                break;

            // call login validation method
            case R.id.rL_do_login :
                if (isLoginButtonEnabled)
                {
                    isFromLocation=true;
                    permissionsArray =new String[]{ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION};
                    runTimePermission=new RunTimePermission(mActivity, permissionsArray,false);
                    showKeyboard(InputMethodManager.HIDE_IMPLICIT_ONLY);

                    LocationManager lm = (LocationManager)mActivity.getSystemService(Context.LOCATION_SERVICE);
                    boolean isLocationEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    System.out.println(TAG+" "+"is location enabled="+ isLocationEnabled +" "+"is permission allowed="+runTimePermission.checkPermissions(permissionsArray));

                    progress_bar_login.setVisibility(View.VISIBLE);
                    tV_do_login.setVisibility(View.GONE);



                    if (isLocationEnabled && runTimePermission.checkPermissions(permissionsArray))
                        getCurrentLocation();
                    else
                        loginRequestApi();
                    // loginRequestApi();
                }
                break;

            // User profile pic
            case R.id.iV_profile_pic:
                System.out.println(TAG+" "+"profile pic clicked..");
                isFromLocation=false;
                permissionsArray =new String[]{CAMERA,WRITE_EXTERNAL_STORAGE};
                runTimePermission=new RunTimePermission(mActivity, permissionsArray,false);
                if (runTimePermission.checkPermissions(permissionsArray))
                    chooseImage();
                else
                {
                    runTimePermission.requestPermission();
                }
                break;

            // Register user
            case R.id.rL_createAcc:

                if (isSignUpButtonEnabled) {
                    if (checkboxSignUp.isChecked()) {



                        if (isUserRegistered) {
                            CommonClass.showSnackbarMessage(rL_rootElement, getResources().getString(R.string.username_already_registered));
                        } else if (isPhoneRegistered) {
                            CommonClass.showSnackbarMessage(rL_rootElement, getResources().getString(R.string.phone_already_registered));
                        } else if (isEmailRegistered) {
                            CommonClass.showSnackbarMessage(rL_rootElement, getResources().getString(R.string.email_already_registered));
                        } else {
                            //generateOtp();

                            System.out.println(TAG+" "+"type="+type);

                            switch (type) {
                                // Normal sign up
                                case "normalSignup":
                                    signUpType = VariableConstants.TYPE_MANUAL;
                                    if (isPictureTaken) {
                                        progress_bar_signup.setVisibility(View.VISIBLE);
                                        tV_signup.setVisibility(View.GONE);
                                        getCloudinaryDetailsApi();
                                    } else {
                                        openOtpScreen("");
                                    }
                                    break;

                                // Google sign up
                                case "googleSignUp":
                                    signUpType = VariableConstants.TYPE_GOOGLE;
                                    if (isPictureTaken) {
                                        progress_bar_signup.setVisibility(View.VISIBLE);
                                        tV_signup.setVisibility(View.GONE);
                                        getCloudinaryDetailsApi();
                                    } else {
                                        openOtpScreen(google_userImageUrl);
                                    }
                                    break;

                                // Facebook signup
                                case "fbSignUp":
                                    signUpType = VariableConstants.TYPE_FACEBOOK;
                                    if (isPictureTaken) {
                                        progress_bar_signup.setVisibility(View.VISIBLE);
                                        tV_signup.setVisibility(View.GONE);
                                        getCloudinaryDetailsApi();
                                    } else {
                                        openOtpScreen(fb_userImageUrl);
                                    }
                                    break;
                            }
                        }
                    }
                    else
                        CommonClass.showTopSnackBar(rL_rootElement,getResources().getString(R.string.please_accept_termsNconditions));
                }
                break;

            // open country picker dialog
            case R.id.rL_country_picker :
                if (dialogCountryList!=null)
                {
                    showKeyboard(InputMethodManager.SHOW_FORCED);
                    dialogCountryList.showCountryCodePicker(new SetCountryCodeListener() {
                        @Override
                        public void getCode(String code, String name) {
                            showKeyboard(InputMethodManager.HIDE_IMPLICIT_ONLY);
                            countryIsoNumber=getResources().getString(R.string.plus) + code;
                            code=getResources().getString(R.string.plus) + code;
                            tV_country_iso_no.setText(code);
                            tV_country_code.setText(name);
                            eT_mobileNo.requestFocus();
                        }
                    });
                }
                break;

            // forgot password
            case R.id.rL_forgot_password :
                if (isToStartActivity) {
                    startActivity(new Intent(mActivity, ForgotPasswordActivity.class));
                    isToStartActivity = false;
                }
                break;
        }
    }

    /**
     * <h>GenerateOtp</h>
     */
    private void generateOtp()
    {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            progress_bar_signup.setVisibility(View.VISIBLE);
            tV_signup.setVisibility(View.GONE);

            System.out.println(TAG+" "+"country code="+tV_country_iso_no.getText().toString());

            JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("deviceId",mSessionManager.getDeviceId());
                request_datas.put("phoneNumber", countryIsoNumber+eT_mobileNo.getText().toString());
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.OTP, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag)
                {
                    System.out.println(TAG+" "+"otp response="+result);
                    progress_bar_signup.setVisibility(View.GONE);
                    tV_signup.setVisibility(View.VISIBLE);
                    Gson gson = new Gson();
                    PhoneOtpMainPojo otpMainPojo = gson.fromJson(result, PhoneOtpMainPojo.class);

                    // success
                    switch (otpMainPojo.getCode())
                    {
                        // success
                        case "200":
                            otpCode=otpMainPojo.getData();
                            System.out.println(TAG+" "+"type="+type);

                            switch (type) {
                                // Normal sign up
                                case "normalSignup":
                                    signUpType = VariableConstants.TYPE_MANUAL;
                                    if (isPictureTaken) {
                                        progress_bar_signup.setVisibility(View.VISIBLE);
                                        tV_signup.setVisibility(View.GONE);
                                        getCloudinaryDetailsApi();
                                    } else {
                                        openOtpScreen("");
                                    }
                                    break;

                                // Google sign up
                                case "googleSignUp":
                                    signUpType = VariableConstants.TYPE_GOOGLE;
                                    if (isPictureTaken) {
                                        progress_bar_signup.setVisibility(View.VISIBLE);
                                        tV_signup.setVisibility(View.GONE);
                                        getCloudinaryDetailsApi();
                                    } else {
                                        openOtpScreen(google_userImageUrl);
                                    }
                                    break;

                                // Facebook signup
                                case "fbSignUp":
                                    signUpType = VariableConstants.TYPE_FACEBOOK;
                                    if (isPictureTaken) {
                                        progress_bar_signup.setVisibility(View.VISIBLE);
                                        tV_signup.setVisibility(View.GONE);
                                        getCloudinaryDetailsApi();
                                    } else {
                                        openOtpScreen(fb_userImageUrl);
                                    }
                                    break;
                            }
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
                    progress_bar_signup.setVisibility(View.GONE);
                    tV_signup.setVisibility(View.VISIBLE);
                    CommonClass.showSnackbarMessage(rL_rootElement, error);
                }
            });
        }
        else CommonClass.showSnackbarMessage(rL_rootElement,getResources().getString(R.string.NoInternetAccess));
    }

    private void openOtpScreen(String profilePicUrl)
    {
        // stop progress bar
        progress_bar_signup.setVisibility(View.GONE);
        tV_signup.setVisibility(View.VISIBLE);

        // Hide Keypad
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        // call Number verification screen
        Intent intent = new Intent(mActivity, NumberVerificationActivity.class);
        intent.putExtra("type",type);
        intent.putExtra("signupType",signUpType);
        intent.putExtra("username",eT_userName.getText().toString());
        intent.putExtra("profilePicUrl",profilePicUrl);
        intent.putExtra("fullName",eT_fullName.getText().toString());
        intent.putExtra("password",eT_password.getText().toString());
        intent.putExtra("phoneNumber",countryIsoNumber+eT_mobileNo.getText().toString());
        intent.putExtra("email",eT_emailId.getText().toString());
        intent.putExtra("googleToken",googleToken);
        intent.putExtra("googleId",googleId);
        intent.putExtra("facebookId",fbId);
        intent.putExtra("accessToken",fb_accessToken);
        intent.putExtra("otpCode",otpCode);
        if (isToStartActivity) {
            startActivityForResult(intent, VariableConstants.NUMBER_VERIFICATION_REQ_CODE);
            isToStartActivity = false;
            System.out.println(TAG + " " + "sending mob no=" + countryIsoNumber + eT_mobileNo.getText().toString());
        }
    }

    /**
     * In this method we find current location using FusedLocationApi.
     * in this we have onUpdateLocation() method in which we check if
     * its not null then We call guest user api.
     */
    private void getCurrentLocation()
    {
        if(CommonClass.isNetworkAvailable(mActivity)) {
            locationService = new FusedLocationService(mActivity, new FusedLocationReceiver() {
                @Override
                public void onUpdateLocation() {
                    Location currentLocation = locationService.receiveLocation();
                    if (currentLocation != null) {
                        currentLat = String.valueOf(currentLocation.getLatitude());
                        currentLng = String.valueOf(currentLocation.getLongitude());

                        System.out.println(TAG+" "+"currentLat="+ currentLat +" "+"currentLng="+ currentLng);

                        if (isLocationFound(currentLat, currentLng)) {
                            mSessionManager.setCurrentLat(currentLat);
                            mSessionManager.setCurrentLng(currentLng);
                            address = CommonClass.getCompleteAddressString(mActivity, currentLocation.getLatitude(), currentLocation.getLongitude());
                            city = CommonClass.getCityName(mActivity, currentLocation.getLatitude(), currentLocation.getLongitude());
                            countryShortName=CommonClass.getCountryCode(mActivity,currentLocation.getLatitude(), currentLocation.getLongitude());

                            String country=CommonClass.getCountryName(mActivity,currentLocation.getLatitude(), currentLocation.getLongitude());
                            if(address.isEmpty() || address==null){
                                String c=city.substring(0,1).toUpperCase()+""+city.substring(1);
                                address=c+", "+country;
                            }

                            loginRequestApi();
                        }
                    }
                }
            }
            );
        }
        else
        {
            progress_bar_login.setVisibility(View.GONE);
            tV_do_login.setVisibility(View.VISIBLE);
            CommonClass.showSnackbarMessage(rL_rootElement,getResources().getString(R.string.NoInternetAccess));
        }
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
        if (!isPictureTaken)
        {
            iv_edit_icon.setVisibility(View.GONE);
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
        isPictureTaken=false;
        iv_edit_icon.setVisibility(View.GONE);
        iV_profile_pic.setImageResource(R.drawable.add_photo);
    }

    /**
     * <h>LoginRequestApi</h>
     * <p>
     *     This method is called when user click on Normal login button.
     *     In this method we used to call login api through OkHttp3. After
     *     getting response if the code is 200. Then we move to HomePageActivity.
     * </p>
     */
    private void loginRequestApi()
    {
        //username, password
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            JSONObject requestDatas=new JSONObject();
            // loginType, pushToken, place, city, countrySname, latitude, longitude,username, password
            try {
                requestDatas.put("loginType",VariableConstants.TYPE_MANUAL);
                requestDatas.put("pushToken",mSessionManager.getPushToken());
                requestDatas.put("place",address);
                requestDatas.put("city",city);
                requestDatas.put("countrySname",countryShortName);
                requestDatas.put("latitude", currentLat);
                requestDatas.put("longitude", currentLng);
                requestDatas.put("username", eT_loginUserName.getText().toString());
                requestDatas.put("password", eT_loginPassword.getText().toString());
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.LOGIN, OkHttp3Connection.Request_type.POST, requestDatas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    LoginResponsePojo loginResponse;
                    Gson gson=new Gson();
                    loginResponse=gson.fromJson(result,LoginResponsePojo.class);

                    switch (loginResponse.getCode())
                    {
                        // Success
                        case "200":


                            mSessionManager.setComingFrom( "login" );
                            mSessionManager.setIsUserLoggedIn(true);
                            mSessionManager.setmqttId(loginResponse.getMqttId());
                            mSessionManager.setAuthToken(loginResponse.getToken());
                            mSessionManager.setUserName(loginResponse.getUsername());
                            mSessionManager.setUserImage(loginResponse.getProfilePicUrl());
                            mSessionManager.setUserId(loginResponse.getUserId());
                            mSessionManager.setLoginWith("normalLogin");
                            initUserDetails(loginResponse.getProfilePicUrl(),loginResponse.getMqttId(),loginResponse.getEmail(),loginResponse.getUsername(),loginResponse.getToken());
                            logDeviceInfo(loginResponse.getToken());
                            break;

                        // Error
                        default:
                            progress_bar_login.setVisibility(View.GONE);
                            tV_do_login.setVisibility(View.VISIBLE);
                            CommonClass.showTopSnackBar(rL_rootElement,loginResponse.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    progress_bar_login.setVisibility(View.GONE);
                    tV_do_login.setVisibility(View.VISIBLE);
                    CommonClass.showTopSnackBar(rL_rootElement,error);
                }
            });
        }
        else
        {
            progress_bar_login.setVisibility(View.GONE);
            tV_do_login.setVisibility(View.VISIBLE);
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
                    progress_bar_login.setVisibility(View.GONE);
                    System.out.println(TAG+" "+"log device info="+result);

                    LogDevicePojo logDevicePojo;
                    Gson gson=new Gson();
                    logDevicePojo=gson.fromJson(result,LogDevicePojo.class);

                    switch (logDevicePojo.getCode())
                    {
                        // success
                        case "200" :
                            // Open Home page screen
                            //LandingActivity.mLandingActivity.finish();
                            //finish();


                            SessionManager sessionManager = new SessionManager( LoginOrSignupActivity.this );
                            sessionManager.setUserMarketOwner( is_market );

                            Intent intent = new Intent();
                            intent.putExtra("isToFinishLandingScreen",true);
                            setResult(VariableConstants.LOGIN_SIGNUP_REQ_CODE,intent);
                            finish();
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // error
                        default:
                            tV_do_login.setVisibility(View.VISIBLE);
                            CommonClass.showSnackbarMessage(rL_rootElement,logDevicePojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    progress_bar_login.setVisibility(View.GONE);
                    tV_do_login.setVisibility(View.VISIBLE);
                    CommonClass.showSnackbarMessage(rL_rootElement,error);
                }
            });
        }
        else CommonClass.showSnackbarMessage(rL_rootElement,getResources().getString(R.string.NoInternetAccess));
    }

    /**
     * <h>phoneNumberCheckApi</h>
     * <p>
     *     In this method we used to do phone Number Check api
     *     to find the given phone number is already exist or
     *     not.
     * </p>
     */
    private void phoneNumberCheckApi()
    {
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            progress_bar_ph.setVisibility(View.VISIBLE);
            iV_error_ph.setVisibility(View.GONE);
            JSONObject request_param=new JSONObject();
            try {
                request_param.put("phoneNumber",countryIsoNumber+eT_mobileNo.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.PHONE_NUMBER_CHECK, OkHttp3Connection.Request_type.POST, request_param, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    progress_bar_ph.setVisibility(View.GONE);
                    System.out.println(TAG+" "+"phoneNumber api res="+result);

                    PhoneNoCheckPojo phoneNoCheckPojo;
                    Gson gson=new Gson();
                    phoneNoCheckPojo=gson.fromJson(result,PhoneNoCheckPojo.class);

                    switch (phoneNoCheckPojo.getCode())
                    {
                        // success
                        case "200" :
                            isPhoneRegistered=false;
                            iV_error_ph.setVisibility(View.VISIBLE);
                            iV_error_ph.setImageResource(R.drawable.rightusername);
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // error
                        default:
                            isPhoneRegistered=true;
                            iV_error_ph.setVisibility(View.VISIBLE);
                            iV_error_ph.setImageResource(R.drawable.error_icon);
                            CommonClass.showTopSnackBar(rL_rootElement,phoneNoCheckPojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    progress_bar_ph.setVisibility(View.GONE);
                }
            });
        }
    }

    /**
     * <h>UserNameCheckApi</h>
     * <p>
     *     In this method we used to do user name Check api
     *     to find the given user name is already exist or
     *     not.
     * </p>
     */
    private void userNameCheckApi()
    {
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            pBar_userName.setVisibility(View.VISIBLE);
            iV_userName_error.setVisibility(View.GONE);
            JSONObject request_param=new JSONObject();
            try {
                request_param.put("username",eT_userName.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.USER_NAME_CHECK, OkHttp3Connection.Request_type.POST, request_param, new OkHttp3Connection.OkHttp3RequestCallback()
            {
                @Override
                public void onSuccess(String result, String user_tag) {
                    pBar_userName.setVisibility(View.GONE);
                    System.out.println(TAG+" "+"user name check api res="+result);

                    UserNameCheckPojo userNameCheckPojo;
                    Gson gson=new Gson();
                    userNameCheckPojo=gson.fromJson(result,UserNameCheckPojo.class);

                    switch (userNameCheckPojo.getCode())
                    {
                        // success i.e user is not registered
                        case "200" :
                            isUserRegistered=false;
                            iV_userName_error.setVisibility(View.VISIBLE);
                            iV_userName_error.setImageResource(R.drawable.rightusername);
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // error like user is already registered
                        default:
                            isUserRegistered=true;
                            iV_userName_error.setVisibility(View.VISIBLE);
                            iV_userName_error.setImageResource(R.drawable.error_icon);
                            CommonClass.showTopSnackBar(rL_rootElement,userNameCheckPojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    pBar_userName.setVisibility(View.GONE);
                }
            });
        }
    }

    /**
     * <h>UserNameCheckApi</h>
     * <p>
     *     In this method we used to do user name Check api
     *     to find the given user name is already exist or
     *     not.
     * </p>
     */
    private void emailCheckApi()
    {
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            pBar_email.setVisibility(View.VISIBLE);
            iV_error_email.setVisibility(View.GONE);
            JSONObject request_param=new JSONObject();
            try {
                request_param.put("email",eT_emailId.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.EMAIL_ID_CHECK, OkHttp3Connection.Request_type.POST, request_param, new OkHttp3Connection.OkHttp3RequestCallback()
            {
                @Override
                public void onSuccess(String result, String user_tag) {
                    pBar_email.setVisibility(View.GONE);
                    System.out.println(TAG+" "+"email is check api res="+result);

                    EmailCheckPojo emailCheckPojo;
                    Gson gson=new Gson();
                    emailCheckPojo=gson.fromJson(result,EmailCheckPojo.class);

                    switch (emailCheckPojo.getCode())
                    {
                        // success i.e email is not registered
                        case "200" :
                            isEmailRegistered=false;
                            iV_error_email.setVisibility(View.VISIBLE);
                            iV_error_email.setImageResource(R.drawable.rightusername);
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;
                        // error like email is already registered
                        default:
                            isEmailRegistered=true;
                            iV_error_email.setVisibility(View.VISIBLE);
                            iV_error_email.setImageResource(R.drawable.error_icon);
                            CommonClass.showTopSnackBar(rL_rootElement,emailCheckPojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    pBar_email.setVisibility(View.GONE);
                }
            });
        }
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
                public void onSuccess(String result, String tag)
                {

                    Cloudinary_Details_reponse response = new Gson().fromJson(result, Cloudinary_Details_reponse.class);
                    if (response.getCode().equals("200")) {
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
                    }
                    else
                    {
                        progress_bar_signup.setVisibility(View.GONE);
                        tV_signup.setVisibility(View.VISIBLE);
                        CommonClass.showTopSnackBar(rL_rootElement,response.getMessage());
                    }
                }

                @Override
                public void onError(String error, String tag)
                {
                    progress_bar_signup.setVisibility(View.GONE);
                    tV_signup.setVisibility(View.VISIBLE);
                    CommonClass.showTopSnackBar(rL_rootElement,error);
                }
            });
        } else CommonClass.showTopSnackBar(rL_rootElement,getResources().getString(R.string.NoInternetAccess));
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
        //cloudData.setPath(handleCameraEvent.getRealPathFromURI());
        cloudData.setPath(mFile.getAbsolutePath());
        cloudData.setVideo(false);

        new UploadToCloudinary(remaining_data, cloudData)
        {
            @Override
            public void callBack(Map resultData) {
                if (resultData != null) {
                    String main_url = (String) resultData.get("url");
                    System.out.println(TAG + " " + "main url=" + main_url);
                    openOtpScreen(main_url);
                    //getCurrentLocation(main_url);
                }
            }

            @Override
            public void errorCallBack(String error) {

            }
        };
    }

    /**
     * <h>ShowKeyboard</h>
     * <p>
     *     In this method we used to open device keypad when user
     *     click on search iocn and close when user click close
     *     search button.
     * </p>
     * @param flag This is integer valuew to open or close keypad
     */
    private void showKeyboard(int flag)
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(flag,0);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(TAG+" "+"on activity result..."+" "+"requestCode="+requestCode+" "+"result code="+requestCode+"data="+data);


        Bitmap bitmap;

        switch (requestCode) {
            // Gallery
            case VariableConstants.SELECT_GALLERY_IMG_REQ_CODE:
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
                String path = data.getStringExtra(CropImage.IMAGE_PATH);
                if (path == null) {
                    return;
                }
                isPictureTaken = true;
                iv_edit_icon.setVisibility(View.VISIBLE);
                bitmap = BitmapFactory.decodeFile(mFile.getPath());
                iV_profile_pic.setImageBitmap(mHandleCameraEvents.getCircleBitmap(bitmap));
                break;

            // To finish Current screen
            case VariableConstants.NUMBER_VERIFICATION_REQ_CODE :
                if(data!=null) {
                    boolean isToFinishLoginSignup = data.getBooleanExtra("isToFinishLoginSignup", true);
                    System.out.println(TAG + "isToFinishLoginSignup=" + isToFinishLoginSignup);
                    if (isToFinishLoginSignup) {
                        Intent intent = new Intent();
                        intent.putExtra("isToFinishLandingScreen", true);
                        intent.putExtra("isFromSignup", true);
                        setResult(VariableConstants.LOGIN_SIGNUP_REQ_CODE, intent);
                        finish();
                    }
                }
                break;
        }
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
                        if (isFromLocation)
                        {
                            getCurrentLocation();
                        }else if(isAskLocationPermission){
                            Log.d("Location Permission", "Granted");
                        }
                        else
                            chooseImage();
                    }
                }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }
    /*
    * Initialization of the user details .*/
    private void initUserDetails(String profile_Url,String userId,String email,String userName,String token)
    {
        CouchDbController db =AppController.getInstance().getDbController();
        Map<String, Object> map = new HashMap<>();
        if (profile_Url!=null&&!profile_Url.isEmpty())
        {
            map.put("userImageUrl",profile_Url);
        } else {
            map.put("userImageUrl","");
        }
        map.put("userIdentifier", email);
        map.put("userId",userId);
        map.put("userName",userName);
        map.put("apiToken",token);
        if(userId==null||userId.isEmpty())
        {
            Toast.makeText(this, R.string.userIdtext,Toast.LENGTH_SHORT).show();
           return;
        }

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
}
