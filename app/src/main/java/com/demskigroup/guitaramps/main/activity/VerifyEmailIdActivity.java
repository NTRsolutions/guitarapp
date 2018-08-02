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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.demskigroup.guitaramps.pojo_class.EditProfilePojo;
import com.demskigroup.guitaramps.pojo_class.edit_profile_pojo.EditProfileData;
import com.google.gson.Gson;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.fcm_push_notification.Config;
import com.demskigroup.guitaramps.fcm_push_notification.NotificationMessageDialog;
import com.demskigroup.guitaramps.fcm_push_notification.NotificationUtils;
import com.demskigroup.guitaramps.pojo_class.verify_email_pojo.VerifyEmailMain;
import com.demskigroup.guitaramps.utility.ApiUrl;
import com.demskigroup.guitaramps.utility.CommonClass;
import com.demskigroup.guitaramps.utility.OkHttp3Connection;
import com.demskigroup.guitaramps.utility.SessionManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Timer;
import java.util.TimerTask;
import static com.demskigroup.guitaramps.utility.VariableConstants.VERIFY_EMAIL_REQ_CODE;

/**
 * <h>VerifyEmailIdActivity</h>
 * <p>
 *     In this class we used to change email-id and send email to that given mail id to verify.
 * </p>
 * @since 11-Jul-17
 */
public class VerifyEmailIdActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG=VerifyEmailIdActivity.class.getSimpleName();
    private Activity mActivity;
    private EditText eT_emailId;
    private boolean isSendButtonEnabled;
    private RelativeLayout rL_send;
    private SessionManager mSessionManager;
    private LinearLayout linear_rootElement;
    private ProgressBar progress_bar;
    private TextView tV_send;
    private NotificationMessageDialog mNotificationMessageDialog;
    Intent I =  null;
    public static  String KEY_EMAIL = "key_email";
    String email = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);


         I = getIntent();
         email = I.getStringExtra(KEY_EMAIL);

        initVariables();
        if(email == null) {
            getProfileDatasApi();
        }
    }

    /**
     * <h>InitVariables</h>
     * <p>
     *     In this method we used to initialize all variables.
     * </p>
     */
    private void initVariables()
    {
        mActivity=VerifyEmailIdActivity.this;
        mNotificationMessageDialog=new NotificationMessageDialog(mActivity);
        mSessionManager=new SessionManager(mActivity);
        progress_bar= (ProgressBar) findViewById(R.id.progress_bar);
        tV_send= (TextView) findViewById(R.id.tV_send);

        // root view
        linear_rootElement= (LinearLayout) findViewById(R.id.linear_rootElement);

        // back button
        RelativeLayout rL_back_btn = (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);

        // submit button
        rL_send= (RelativeLayout) findViewById(R.id.rL_send);
        rL_send.setOnClickListener(this);
        CommonClass.setViewOpacity(mActivity,rL_send,102,R.drawable.rect_purple_color_with_solid_shape);

        // EditText Email Address
        eT_emailId= (EditText) findViewById(R.id.eT_emailId);
        if(email != null){
            eT_emailId.setText(email);
        }

        eT_emailId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                String email=eT_emailId.getText().toString();
                if (!email.isEmpty() && CommonClass.isValidEmail(email))
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
     * <h>verifyEmailId</h>
     * <p>
     *     In this method we used to do api call to for eamil verification.
     * </p>
     */
    private void verifyEmailId()
    {
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            progress_bar.setVisibility(View.VISIBLE);
            tV_send.setVisibility(View.GONE);

            JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("email", eT_emailId.getText().toString());
                request_datas.put("token", mSessionManager.getAuthToken());


            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.VERIFY_EMAIL1, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    progress_bar.setVisibility(View.GONE);
                    tV_send.setVisibility(View.VISIBLE);

                    VerifyEmailMain verifyEmailMain;
                    Gson gson=new Gson();
                    verifyEmailMain=gson.fromJson(result,VerifyEmailMain.class);

                    switch (verifyEmailMain.getCode())
                    {
                        case "200" :
                            CommonClass.showSuccessSnackbarMsg(linear_rootElement,verifyEmailMain.getMessage());
                            final Timer t = new Timer();
                            t.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    Intent intent=new Intent();
                                    intent.putExtra("emailId",eT_emailId.getText().toString());
                                    setResult(VERIFY_EMAIL_REQ_CODE,intent);
                                    // when the task active then close the activity
                                    t.cancel();
                                    onBackPressed();
                                }
                            }, 3000);
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;

                        case "500":
                            CommonClass.showSnackbarMessage(linear_rootElement,"Email Updated");

                            final Timer t2 = new Timer();
                            t2.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    Intent intent=new Intent();
                                    intent.putExtra("emailId",eT_emailId.getText().toString());
                                    setResult(VERIFY_EMAIL_REQ_CODE,intent);
                                    // when the task active then close the activity
                                    t2.cancel();
                                    onBackPressed();
                                }
                            }, 3000);
                            break;



                        default:
                            CommonClass.showSnackbarMessage(linear_rootElement,verifyEmailMain.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    progress_bar.setVisibility(View.GONE);
                    tV_send.setVisibility(View.VISIBLE);
                }
            });
        }
        else CommonClass.showSnackbarMessage(linear_rootElement,getResources().getString(R.string.NoInternetAccess));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            // back button
            case R.id.rL_back_btn :
                onBackPressed();
                break;

            // submit
            case R.id.rL_send :
                if (isSendButtonEnabled)
                    verifyEmailId();
                break;

        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }


    private void getProfileDatasApi()
    {
        if (CommonClass.isNetworkAvailable(mActivity))
        {

            progress_bar.setVisibility(View.VISIBLE);
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
                    progress_bar.setVisibility(View.GONE);
                    System.out.println(TAG + " " + "edit profile res=" + result);

                    EditProfilePojo editProfilePojo;
                    Gson gson=new Gson();
                    editProfilePojo=gson.fromJson(result,EditProfilePojo.class);

                    switch (editProfilePojo.getCode())
                    {
                        // success
                        case "200" :

                            EditProfileData editProfileData=editProfilePojo.getData();
                            if (editProfileData!=null)
                            {

                                email=editProfileData.getEmail();
                                eT_emailId.setText(email);

                                // Set user profile pic

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
                    progress_bar.setVisibility(View.GONE);
                }
            });
        }
        else CommonClass.showSnackbarMessage(linear_rootElement,getResources().getString(R.string.NoInternetAccess));

    }
}
