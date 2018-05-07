package com.yelo.com.main.activity;

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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.yelo.com.R;
import com.yelo.com.county_code_picker.Country;
import com.yelo.com.county_code_picker.DialogCountryList;
import com.yelo.com.county_code_picker.SetCountryCodeListener;
import com.yelo.com.fcm_push_notification.Config;
import com.yelo.com.fcm_push_notification.NotificationMessageDialog;
import com.yelo.com.fcm_push_notification.NotificationUtils;
import com.yelo.com.pojo_class.profile_mobile_otp.ProfileNumberOtpMain;
import com.yelo.com.utility.ApiUrl;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.OkHttp3Connection;
import com.yelo.com.utility.SessionManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Locale;

/**
 * <h>VerifyPhoneNoActivity</h>
 * <p>
 *     This class is getting called from EditProfileActivity class. In this class we used to accept the
 *     new mobile number from user and used to send otp to the user, Once we get success from server then
 *     launch the verification screen.
 * </p>
 * @since 10-Jul-17
 */
public class VerifyPhoneNoActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG=VerifyPhoneNoActivity.class.getSimpleName();
    private ArrayList<Country> arrayListCountry;
    private DialogCountryList dialogCountryList;
    private Activity mActivity;
    private String countryIsoNumber="";
    private TextView tV_country_iso_no,tV_country_code,tV_send;
    private EditText eT_mobileNo;
    private SessionManager mSessionManager;
    private ProgressBar progress_bar;
    private LinearLayout linear_rootElement;
    public static Activity verifyNumberInstance;
    private boolean isSendButtonEnabled;
    private NotificationMessageDialog mNotificationMessageDialog;
    private boolean isToStartActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        setContentView(R.layout.activity_verify_number);

        initVariables();
    }

    /**
     * <h>initVariables</h>
     * <p>
     *     In this method we used to initialize all variables.
     * </p>
     */
    private void initVariables()
    {
        verifyNumberInstance=VerifyPhoneNoActivity.this;
        isToStartActivity = true;
        arrayListCountry = new ArrayList<>();
        mActivity=VerifyPhoneNoActivity.this;
        mNotificationMessageDialog=new NotificationMessageDialog(mActivity);
        mSessionManager=new SessionManager(mActivity);
        tV_country_iso_no= (TextView) findViewById(R.id.tV_country_iso_no);
        tV_country_code= (TextView) findViewById(R.id.tV_country_code);
        tV_send= (TextView) findViewById(R.id.tV_send);
        eT_mobileNo= (EditText) findViewById(R.id.eT_mobileNo);
        progress_bar= (ProgressBar) findViewById(R.id.progress_bar);
        progress_bar.setVisibility(View.GONE);
        linear_rootElement= (LinearLayout) findViewById(R.id.linear_rootElement);

        // open currency picker dialog
        RelativeLayout rL_country_picker = (RelativeLayout) findViewById(R.id.rL_country_picker);
        rL_country_picker.setOnClickListener(this);

        // Back button
        RelativeLayout rL_back_btn= (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);

        final RelativeLayout rL_send = (RelativeLayout) findViewById(R.id.rL_send);
        rL_send.setOnClickListener(this);

        // EditText Email Address
        CommonClass.setViewOpacity(mActivity,rL_send,102,R.drawable.rect_purple_color_with_solid_shape);
        eT_mobileNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                String mobileNo=eT_mobileNo.getText().toString();
                if (!mobileNo.isEmpty() && mobileNo.length()==10)
                {
                    isSendButtonEnabled=true;
                    CommonClass.setViewOpacity(mActivity,rL_send,204,R.drawable.rect_purple_color_with_solid_shape);
                }
                else
                {
                    isSendButtonEnabled=false;
                    CommonClass.setViewOpacity(mActivity,rL_send,102,R.drawable.rect_purple_color_with_solid_shape);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // call method to assign currency
        getCountryCodeList();
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
                    tV_country_iso_no.setText(countryIsoNo);
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
        for (Country country : arrayListCountry) {
            if (country.getName().equals(isoCode))
            {
                countryCode = country.getCode();
                return countryCode;
            }
        }
        return countryCode;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            // open currency picker dialog
            case R.id.rL_country_picker :
                if (dialogCountryList!=null)
                {
                    dialogCountryList.showCountryCodePicker(new SetCountryCodeListener() {
                        @Override
                        public void getCode(String code, String name) {
                            countryIsoNumber=getResources().getString(R.string.plus) + code;
                            code=getResources().getString(R.string.plus) + code;
                            tV_country_iso_no.setText(code);
                            tV_country_code.setText(name);
                        }
                    });
                }
                break;

            // send
            case R.id.rL_send :
                if (isSendButtonEnabled)
                generateProfileMobOtp();
                break;

            // back button
            case R.id.rL_back_btn :
                onBackPressed();
                break;
        }
    }

    /**
     * <h>generateProfileMobOtp</h>
     * <p>
     *     In this method we used to send call generate otp api to send otp to given
     *     mobile number.
     * </p>
     */
    private void generateProfileMobOtp()
    {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            progress_bar.setVisibility(View.VISIBLE);
            tV_send.setVisibility(View.GONE);
            JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("token", mSessionManager.getAuthToken());
                request_datas.put("phoneNumber", countryIsoNumber + eT_mobileNo.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.OTP_PROFILE_NUMBER, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag)
                {
                    progress_bar.setVisibility(View.GONE);
                    tV_send.setVisibility(View.VISIBLE);
                    System.out.println(TAG+" "+"generate profile otp res="+result);

                    ProfileNumberOtpMain profileNumberOtpMain;
                    Gson gson=new Gson();
                    profileNumberOtpMain=gson.fromJson(result,ProfileNumberOtpMain.class);

                    switch (profileNumberOtpMain.getCode())
                    {
                        // success
                        case "200" :
                            if (isToStartActivity) {
                                isToStartActivity = false;
                                String otp = CommonClass.extractNumberFromString(profileNumberOtpMain.getData().getBody());
                                System.out.println(TAG + " " + "profile otp=" + otp);
                                Intent intent = new Intent(mActivity, ProfileNumberVerifyActivity.class);
                                intent.putExtra("otpCode", otp);
                                intent.putExtra("phoneNumber", countryIsoNumber + eT_mobileNo.getText().toString());
                                startActivity(intent);
                            }
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // error
                        default:
                            CommonClass.showSnackbarMessage(linear_rootElement,profileNumberOtpMain.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    progress_bar.setVisibility(View.GONE);
                    tV_send.setVisibility(View.VISIBLE);
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
}
