package com.demskigroup.guitaramps.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import com.google.gson.Gson;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.adapter.ViewPagerAdapter;
import com.demskigroup.guitaramps.fcm_push_notification.Config;
import com.demskigroup.guitaramps.fcm_push_notification.NotificationMessageDialog;
import com.demskigroup.guitaramps.fcm_push_notification.NotificationUtils;
import com.demskigroup.guitaramps.main.view_pager.notification_frag.FollowingFrag;
import com.demskigroup.guitaramps.main.view_pager.notification_frag.YouFrag;
import com.demskigroup.guitaramps.pojo_class.fcm_notification_pojo.FcmNotificationBody;
import com.demskigroup.guitaramps.utility.CommonClass;
import com.demskigroup.guitaramps.utility.DialogBox;
import com.demskigroup.guitaramps.utility.VariableConstants;

/**
 * <h>NotificationActivity</h>
 * <p>
 *     In this class we used to show the all notification for friends and
 *     logged-in user.
 * </p>
 * @since 4/15/2017
 * @version 1.0
 * @author 3Embed
 */
public class NotificationActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = NotificationActivity.class.getSimpleName();
    private boolean isFromNotification;
    private NotificationMessageDialog mNotificationMessageDialog;
    private DialogBox mDialogBox;
    public boolean isNotificationSeen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_notification);
        overridePendingTransition(R.anim.slide_up, R.anim.stay );

        Activity mActivity = NotificationActivity.this;
        isNotificationSeen=false;
        mDialogBox = new DialogBox(mActivity);
        mNotificationMessageDialog=new NotificationMessageDialog(mActivity);
        CommonClass.statusBarColor(mActivity);

        // Receive datas from last activity
        Intent intent=getIntent();
        isFromNotification=intent.getBooleanExtra("isFromNotification",false);
        System.out.println(TAG+" "+"isFromNotification="+isFromNotification);
        String notificationDatas = intent.getStringExtra("notificationDatas");
        handleNotificationDatas(notificationDatas);

        RelativeLayout rL_back_btn = (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);

        ViewPager chatViewPager= (ViewPager)findViewById(R.id.viewpager);
        TabLayout tabs_notification= (TabLayout)findViewById(R.id.tabs_notification);
        setupViewPager(chatViewPager);
        tabs_notification.setupWithViewPager(chatViewPager);
    }

    /**
     * <h>HandleNotificationDatas</h>
     * <p>
     *     In this method we used to check the type of push notification.
     *     if it is of campaign type i.e type 73. then open a dialog.
     * </p>
     * @param notificationDatas consisting the json type String which contains notification complete description.
     */
    private void handleNotificationDatas(String notificationDatas)
    {
        if (notificationDatas!=null && !notificationDatas.isEmpty())
        {
            FcmNotificationBody fcmNotificationBody;
            Gson gson = new Gson();
            fcmNotificationBody = gson.fromJson(notificationDatas,FcmNotificationBody.class);

            if (fcmNotificationBody!=null)
            {
                String campaignId, imageUrl, title, message, type, userId, username,url;
                campaignId=fcmNotificationBody.getCampaignId();
                imageUrl = fcmNotificationBody.getImageUrl();
                title = fcmNotificationBody.getTitle();
                message = fcmNotificationBody.getMessage();
                type= fcmNotificationBody.getType();
                userId=fcmNotificationBody.getUserId();
                username=fcmNotificationBody.getUsername();
                url=fcmNotificationBody.getUrl();

                if (type.equals("73"))
                {
                    mDialogBox.localCampaignDialog(username,userId,campaignId,imageUrl,title,message,url);
                }
            }
        }
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
     * <h>SetupViewPager</h>
     * <p>
     *     In this method we used to set-up viewpager with fragments.
     * </p>
     * @param viewPager The reference of Viewpager
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FollowingFrag(),getResources().getString(R.string.followings));
        adapter.addFragment(new YouFrag(),getResources().getString(R.string.you));
        viewPager.setAdapter(adapter);
        //..remove for redirect you tab
        //if (isFromNotification)
        viewPager.setCurrentItem(1);
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent();
        intent.putExtra("isNotificationSeen",isNotificationSeen);
        setResult(VariableConstants.IS_NOTIFICATION_SEEN_REQ_CODE,intent);
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.rL_back_btn :
                onBackPressed();
                break;
        }
    }
}
