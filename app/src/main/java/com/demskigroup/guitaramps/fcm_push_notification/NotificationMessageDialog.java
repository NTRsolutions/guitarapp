package com.demskigroup.guitaramps.fcm_push_notification;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.fcm_push_notification.Config;
import com.demskigroup.guitaramps.pojo_class.fcm_notification_pojo.FcmNotificationMain;
import com.demskigroup.guitaramps.utility.CommonClass;
import com.demskigroup.guitaramps.utility.DialogBox;
import com.demskigroup.guitaramps.utility.RoundedCornersTransform;
import com.demskigroup.guitaramps.utility.SessionManager;

/**
 * <h>NotificationMessageDialog</h>
 * <p>
 *     In this class We used to initialize broadcast variable. Once we get the value
 *     which has been broadcasted from Notification screen. Then we used to open a dialog
 *     to show local campaign message.
 * </p>
 * @since 25-Aug-17
 */
public class NotificationMessageDialog
{
    private static final String TAG =NotificationMessageDialog.class.getSimpleName();
    public BroadcastReceiver mRegistrationBroadcastReceiver;
    private DialogBox dialogBox;
    private SessionManager mSessionManager;

    public NotificationMessageDialog(Activity mActivity) {
        mSessionManager=new SessionManager(mActivity);
        dialogBox=new DialogBox(mActivity);
        initFcmBroadCastVar();
    }

    /**
     * <h>InitFcmBroadCastVar</h>
     * <p>
     *     In this method we used to initialize Broadcast receiver value. Once we get the
     *     notification then check whether it is json response then show the local campaign
     *     dialog.
     * </p>
     */
    private void initFcmBroadCastVar()
    {
        mRegistrationBroadcastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE))
                {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                }
                else if (intent.getAction().equals(Config.PUSH_NOTIFICATION))
                {
                    // new push notification is received
                    String message = intent.getStringExtra("message");
                    String jsonMessageResponse=intent.getStringExtra("jsonMessage");

                    if (jsonMessageResponse!=null && !jsonMessageResponse.isEmpty())
                    {
                        System.out.println(TAG+" "+"json message="+jsonMessageResponse);
                        Gson gson = new Gson();
                        FcmNotificationMain fcmNotificationMain = gson.fromJson(jsonMessageResponse, FcmNotificationMain.class);

                        String campaignId, imageUrl, title, jsonMessage, type, userId, url, username, timestamp = "";

                        campaignId = fcmNotificationMain.getBody().getCampaignId();
                        imageUrl = fcmNotificationMain.getBody().getImageUrl();
                        title = fcmNotificationMain.getBody().getTitle();
                        jsonMessage = fcmNotificationMain.getBody().getMessage();
                        type = fcmNotificationMain.getBody().getType();
                        userId = fcmNotificationMain.getBody().getUserId();
                        url=fcmNotificationMain.getBody().getUrl();
                        username = fcmNotificationMain.getBody().getUsername();

                        if (type!=null && type.equals("73"))
                        {
                            //final String username, final String userId, final String campaignId, String imageUrl, String title, String message, final String url
                            if(mSessionManager.getIsUserLoggedIn())
                            dialogBox.localCampaignDialog(username,userId,campaignId,imageUrl,title,jsonMessage,url);
                        }
                    }
                }
            }
        };
    }
}
