package com.yelo.com.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.yelo.com.R;
import com.yelo.com.mqttchat.AppController;
import com.yelo.com.utility.CommonClass;
import com.yelo.com.utility.OkHttp3Connection;
import com.yelo.com.utility.SessionManager;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.fabric.sdk.android.Fabric;

/**
 * <h>SplashActivity</h>
 * <p>
 *     This is launch screen i.e open first when user launch the app. It stays for
 *     3second then check if user is logged-in then go to HomeActivity or else go
 *     to Landing Screen where we have option for login or signup.
 * </p>
 * @since 3/29/2017.
 * @author 3Embed
 * @version 1.0
 */
public class SplashActivity extends AppCompatActivity
{
    private static final String TAG = SplashActivity.class.getSimpleName();
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature( Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash);

        mActivity=SplashActivity.this;

        // change status bar color
        CommonClass.statusBarColor(mActivity);
        SessionManager mSessionManager = new SessionManager(mActivity);

        // generating unique id from FCM
        String serialNumber = FirebaseInstanceId.getInstance().getId();
        System.out.println(TAG+" "+"serial number="+serialNumber);
        if (serialNumber!=null && !serialNumber.isEmpty())
        {
            mSessionManager.setDeviceId(serialNumber);
        }

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        //Displaying token on logcat
        System.out.println(TAG+" "+ "My Refreshed token: " + refreshedToken);
        if (refreshedToken!=null && !refreshedToken.isEmpty())
            mSessionManager.setPushToken(refreshedToken);

        System.out.println(TAG+"get push token="+ mSessionManager.getPushToken());

        // get bundle datas if notification comes in background
        Bundle bundle =getIntent().getExtras();
        System.out.println(TAG+" "+"bundle="+bundle);

        String notificationDatas="";
        if (bundle!=null)
        {
            notificationDatas= bundle.getString("body");
            System.out.println(TAG+" "+"bundle notification datas="+notificationDatas);
        }


        // Go to notification screen if any notification msg is there else Home Page
        if (notificationDatas!=null && !notificationDatas.isEmpty())
            callNotificationClass(notificationDatas);
        else
        setTimerForScreen();
    }


    public void forceCrash(View view) {
        throw new RuntimeException("This is a crash");
    }

    /**
     * <h>CallNotificationClass</h>
     * <p>
     *     In this method we used to receive the bundle datas when notification comes
     *     from background. After that we used to send datas to Notification activity
     *     class.
     * </p>
     * @param notificationDatas The notification datas.
     */
    private void callNotificationClass(String notificationDatas)
    {
        if (notificationDatas!=null && !notificationDatas.isEmpty())
        {
            System.out.println(TAG+" "+"bundle="+notificationDatas);
            Intent intent = new Intent(mActivity,NotificationActivity.class);
            intent.putExtra("notificationDatas",notificationDatas);
            intent.putExtra("isFromNotification",true);
            startActivity(intent);
            finish();
        }
    }

    /**
     * <h>SetTimerForScreen</h>
     * <p>
     *     In this method we used to sleep screen for three second.
     * </p>
     */
    private void setTimerForScreen()
    {
        int TIME_OUT = 4000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {



                startActivity(new Intent(mActivity,HomePageActivity.class));
                finish();
            }
        }, TIME_OUT);
    }


}
