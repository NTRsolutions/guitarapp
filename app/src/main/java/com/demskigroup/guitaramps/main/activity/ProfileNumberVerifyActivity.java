package com.demskigroup.guitaramps.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.fcm_push_notification.Config;
import com.demskigroup.guitaramps.fcm_push_notification.NotificationMessageDialog;
import com.demskigroup.guitaramps.fcm_push_notification.NotificationUtils;
import com.demskigroup.guitaramps.pojo_class.profile_mobile_otp.ProfileNumberOtpMain;
import com.demskigroup.guitaramps.pojo_class.update_no_pojo.UpdatePhoneNoPojo;
import com.demskigroup.guitaramps.utility.ApiUrl;
import com.demskigroup.guitaramps.utility.CommonClass;
import com.demskigroup.guitaramps.utility.OkHttp3Connection;
import com.demskigroup.guitaramps.utility.SessionManager;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * <h>ProfileNumberVerifyActivity</h>
 * <p>
 *     This class is getting called from VerifyPhoneNoActivity class. In this class we used
 *     to insert otp which is being sent to our mobile number. If the inserted otp is correct
 *     then call update mobile number api to update the obile number.
 * </p>
 * @since 11-Jul-17
 */
public class ProfileNumberVerifyActivity extends AppCompatActivity implements View.OnClickListener
{
    private Activity mActivity;
    private RelativeLayout rL_rootElement;
    private EditText eT_first_digit,eT_second_digit,eT_third_digit,eT_fourth_digit;
    private RelativeLayout rL_submit;
    private boolean isVerifyButtonVisible;
    private TextView tV_sumit;
    private TextView tV_reSend;
    private static final String TAG=ProfileNumberVerifyActivity.class.getSimpleName();
    private ProgressBar pBar_resend,pBar_submit;
    private String phoneNumber,otpCode;
    private SessionManager mSessionManager;
    private NotificationMessageDialog mNotificationMessageDialog;
    private boolean isToStartActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_verification);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        initVariable();
    }

    /**
     * In this method we used to initialize the all xml and data memeber.
     */
    private void initVariable()
    {
        mActivity= ProfileNumberVerifyActivity.this;
        isToStartActivity = true;
        mNotificationMessageDialog=new NotificationMessageDialog(mActivity);
        mSessionManager=new SessionManager(mActivity);

        // receiving datas from last activity
        Intent intent=getIntent();
        phoneNumber=intent.getStringExtra("phoneNumber");
        otpCode=intent.getStringExtra("otpCode");

        // root element
        rL_rootElement= (RelativeLayout) findViewById(R.id.rL_rootElement);

        // mobile number
        TextView tV_mobile_no = (TextView) findViewById(R.id.tV_mobile_no);
        if (phoneNumber!=null && !phoneNumber.isEmpty())
            tV_mobile_no.setText(phoneNumber);

        // back button
        RelativeLayout rL_back_btn= (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);

        // submit
        tV_sumit= (TextView) findViewById(R.id.tV_sumit);
        rL_submit= (RelativeLayout) findViewById(R.id.rL_submit);
        rL_submit.setOnClickListener(this);
        CommonClass.setViewOpacity(mActivity,rL_submit,102,R.drawable.rect_purple_color_with_solid_shape);

        // resend
        tV_reSend= (TextView) findViewById(R.id.tV_reSend);
        tV_reSend.setOnClickListener(this);

        // progress bar
        pBar_resend= (ProgressBar) findViewById(R.id.pBar_resend);
        pBar_submit= (ProgressBar) findViewById(R.id.pBar_submit);

        // otp
        eT_first_digit= (EditText) findViewById(R.id.eT_first_digit);
        eT_second_digit= (EditText) findViewById(R.id.eT_second_digit);
        eT_third_digit= (EditText) findViewById(R.id.eT_third_digit);
        eT_fourth_digit= (EditText) findViewById(R.id.eT_fourth_digit);

        // visible submit button
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
                    CommonClass.setViewOpacity(mActivity,rL_submit,204,R.drawable.rect_purple_color_with_solid_shape);
                }
                else
                {
                    isVerifyButtonVisible=false;
                    CommonClass.setViewOpacity(mActivity,rL_submit,102,R.drawable.rect_purple_color_with_solid_shape);
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
                    CommonClass.setViewOpacity(mActivity,rL_submit,204,R.drawable.rect_purple_color_with_solid_shape);
                }
                else
                {
                    isVerifyButtonVisible=false;
                    CommonClass.setViewOpacity(mActivity,rL_submit,102,R.drawable.rect_purple_color_with_solid_shape);
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
                    CommonClass.setViewOpacity(mActivity,rL_submit,204,R.drawable.rect_purple_color_with_solid_shape);
                }
                else
                {
                    isVerifyButtonVisible=false;
                    CommonClass.setViewOpacity(mActivity,rL_submit,102,R.drawable.rect_purple_color_with_solid_shape);
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
                    CommonClass.setViewOpacity(mActivity,rL_submit,204,R.drawable.rect_purple_color_with_solid_shape);
                }
                else
                {
                    isVerifyButtonVisible=false;
                    CommonClass.setViewOpacity(mActivity,rL_submit,102,R.drawable.rect_purple_color_with_solid_shape);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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

            // resend
            case R.id.tV_reSend :
                generateProfileMobOtp();
                break;

            case R.id.rL_submit :
                String enteredCode=eT_first_digit.getText().toString()+eT_second_digit.getText().toString()+eT_third_digit.getText().toString()+eT_fourth_digit.getText().toString();
                if (isVerifyButtonVisible)
                {
                    if (enteredCode.equals(otpCode))
                    {
                        updatePhoneNumberApi();
                    }
                    else CommonClass.showTopSnackBar(rL_rootElement,getResources().getString(R.string.invalid_otp_code));
                }
                break;
        }
    }

    /**
     * <h>GenerateProfileMobOtp</h>
     * <p>
     *     In this method we used to call generate otp api to get new otp
     *     when used click on resend button.
     * </p>
     */
    private void generateProfileMobOtp()
    {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            pBar_resend.setVisibility(View.VISIBLE);
            tV_reSend.setVisibility(View.GONE);
            JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("token", mSessionManager.getAuthToken());
                request_datas.put("phoneNumber", phoneNumber);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.OTP_PROFILE_NUMBER, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag)
                {
                    pBar_resend.setVisibility(View.GONE);
                    tV_reSend.setVisibility(View.VISIBLE);
                    System.out.println(TAG+" "+"generate profile re otp res="+result);

                    ProfileNumberOtpMain profileNumberOtpMain;
                    Gson gson=new Gson();
                    profileNumberOtpMain=gson.fromJson(result,ProfileNumberOtpMain.class);

                    switch (profileNumberOtpMain.getCode())
                    {
                        case "200" :
                            otpCode=CommonClass.extractNumberFromString(profileNumberOtpMain.getData().getBody());
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;

                        default:
                            CommonClass.showSnackbarMessage(rL_rootElement,profileNumberOtpMain.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    pBar_resend.setVisibility(View.GONE);
                    tV_reSend.setVisibility(View.VISIBLE);
                }
            });
        }
        else CommonClass.showSnackbarMessage(rL_rootElement,getResources().getString(R.string.NoInternetAccess));
    }

    /**
     * <h>updatePhoneNumberApi</h>
     * <p>
     *     In this method we used to call update mobile number api to update
     *     new mobile number.
     * </p>
     */
    private void updatePhoneNumberApi()
    {
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            pBar_submit.setVisibility(View.VISIBLE);
            tV_sumit.setVisibility(View.GONE);
            JSONObject requestDatas = new JSONObject();
            try {
                requestDatas.put("token", mSessionManager.getAuthToken());
                requestDatas.put("phoneNumber", phoneNumber);
                requestDatas.put("otp",otpCode);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.OTP_PROFILE_NUMBER, OkHttp3Connection.Request_type.PUT, requestDatas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag)
                {
                    pBar_submit.setVisibility(View.GONE);
                    tV_sumit.setVisibility(View.VISIBLE);
                    System.out.println(TAG+" "+"update number res="+result);

                    UpdatePhoneNoPojo updatePhoneNoPojo;
                    Gson gson=new Gson();
                    updatePhoneNoPojo=gson.fromJson(result,UpdatePhoneNoPojo.class);

                    switch (updatePhoneNoPojo.getCode())
                    {
                        // success
                        case "200" :
                            if (isToStartActivity) {
                                isToStartActivity = false;
                                startActivity(new Intent(mActivity, EditProfileActivity.class));
                                EditProfileActivity.editProfileInstance.finish();
                                VerifyPhoneNoActivity.verifyNumberInstance.finish();
                                mActivity.finish();
                            }
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // error
                        default:
                            CommonClass.showSnackbarMessage(rL_rootElement,updatePhoneNoPojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    pBar_submit.setVisibility(View.GONE);
                    tV_sumit.setVisibility(View.VISIBLE);
                }
            });
        }
        else CommonClass.showSnackbarMessage(rL_rootElement,getResources().getString(R.string.NoInternetAccess));
    }
}
