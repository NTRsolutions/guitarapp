package com.demskigroup.guitaramps.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.fcm_push_notification.Config;
import com.demskigroup.guitaramps.fcm_push_notification.NotificationMessageDialog;
import com.demskigroup.guitaramps.fcm_push_notification.NotificationUtils;
import com.demskigroup.guitaramps.utility.CommonClass;
import com.demskigroup.guitaramps.utility.VariableConstants;

/**
 * <h>PostConditionsActivity</h>
 * <p>
 *     In this class we used to show the listing of conditions.
 * </p>
 * @since 2017-05-04
 */
public class PostConditionsActivity extends AppCompatActivity implements View.OnClickListener
{

    private NotificationMessageDialog mNotificationMessageDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conditions);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

        initVariables();
    }

    /**
     * <h>InitVariables</h>
     * <p>
     *     In this method we used to initlialize all variables.
     * </p>
     */
    private void initVariables()
    {
        Activity mActivity = PostConditionsActivity.this;
        mNotificationMessageDialog=new NotificationMessageDialog(mActivity);
        CommonClass.statusBarColor(mActivity);

        TextView tV_description,tV_for_parts,tV_normal_wear,tV_open_box,tV_reconditioned,tV_never_used;
        tV_description= (TextView) findViewById(R.id.tV_description);
        tV_description.setOnClickListener(this);
        tV_for_parts= (TextView) findViewById(R.id.tV_for_parts);
        tV_for_parts.setOnClickListener(this);
        tV_normal_wear= (TextView) findViewById(R.id.tV_normal_wear);
        tV_normal_wear.setOnClickListener(this);
        tV_open_box= (TextView) findViewById(R.id.tV_open_box);
        tV_open_box.setOnClickListener(this);
        tV_reconditioned= (TextView) findViewById(R.id.tV_reconditioned);
        tV_reconditioned.setOnClickListener(this);
        tV_never_used= (TextView) findViewById(R.id.tV_never_used);
        tV_never_used.setOnClickListener(this);

        RelativeLayout rL_back_btn= (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        String conditions;
        switch (v.getId())
        {
            // Other(see description)
            case R.id.tV_description :
                conditions=getResources().getString(R.string.other_see_descriptions);
                setConditionsValues(conditions);
                break;

            // For parts
            case R.id.tV_for_parts :
                conditions=getResources().getString(R.string.for_parts);
                setConditionsValues(conditions);
                break;

            // Used normal wear
            case R.id.tV_normal_wear :
                conditions=getResources().getString(R.string.used_normal_wear);
                setConditionsValues(conditions);
                break;

            // Open box never used
            case R.id.tV_open_box :
                conditions=getResources().getString(R.string.open_box);
                setConditionsValues(conditions);
                break;

            // Reconditioned or certified
            case R.id.tV_reconditioned :
                conditions=getResources().getString(R.string.reconditioned);
                setConditionsValues(conditions);
                break;

            // New never used
            case R.id.tV_never_used :
                conditions=getResources().getString(R.string.new_never_used);
                setConditionsValues(conditions);
                break;

            // back button
            case R.id.rL_back_btn :
                onBackPressed();
                break;
        }
    }

    private void setConditionsValues(String conditions)
    {
        Intent intent=new Intent();
        intent.putExtra("condition",conditions);
        setResult(VariableConstants.CONDITION_REQUEST_CODE,intent);
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }
}
