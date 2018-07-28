package com.demskigroup.guitaramps.fcm_push_notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.main.activity.HomePageActivity;
import com.demskigroup.guitaramps.main.activity.NotificationActivity;
import com.demskigroup.guitaramps.pojo_class.fcm_notification_pojo.FcmNotificationMain;
import com.demskigroup.guitaramps.utility.SessionManager;
import org.json.JSONObject;

/**
 * <h>MyFirebaseMessagingService</h>
 * <p>
 *     In this class we used to receive the Fcm Push notification in onMessageReceived().
 * </p>
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private NotificationUtils notificationUtils;


    private SessionManager mSessionManager;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        mSessionManager = new SessionManager(getApplicationContext());
        String id = mSessionManager.getmqttId()+"/"+remoteMessage.getFrom();
        // Check if message contains a notification payload.

        if(mSessionManager != null) {

            JSONObject json = null;
            String ComingFromId = "";
            if (remoteMessage.getData().size() > 0) {
                Log.e( TAG, "Data Payload: " + remoteMessage.getData().toString() );

                try {
                     json = new JSONObject( remoteMessage.getData().toString() );

                     JSONObject body = new JSONObject(  );
                     body = json.getJSONObject( "body" );

                     ComingFromId = body.optString( "receiverID" );
                } catch (Exception e) {
                    Log.e( TAG, "Exception: " + e.getMessage() );
                }
            }else {
                return;
            }

            if (!ComingFromId.equalsIgnoreCase( mSessionManager.getmqttId() )) {

                if (remoteMessage.getNotification() != null) {
                    Log.e( TAG, "Notification Body: " + remoteMessage.getNotification().getBody() );
                    //handleNotification(remoteMessage.getNotification().getBody());
                    //sendNotification(remoteMessage.getNotification().getBody());

                    Intent resultIntent = new Intent( getApplicationContext(), NotificationActivity.class );//MainActivity
                    resultIntent.putExtra( "message", "message" );
                    resultIntent.putExtra( "isFromNotification", true );
                    tempShowNotificationMessage( getApplicationContext(), getResources().getString( R.string.app_name ), remoteMessage.getNotification().getBody(), "", resultIntent );

                    // app is in foreground, broadcast the push message
                    Intent pushNotification = new Intent( Config.PUSH_NOTIFICATION );
                    pushNotification.putExtra( "message", remoteMessage.getNotification().getBody() );
                    LocalBroadcastManager.getInstance( this ).sendBroadcast( pushNotification );
                }

                // Check if message contains a data payload.
                if (remoteMessage.getData().size() > 0) {
                    Log.e( TAG, "Data Payload: " + remoteMessage.getData().toString() );

                    try {

                        handleDataMessage( json );
                    } catch (Exception e) {
                        Log.e( TAG, "Exception: " + e.getMessage() );
                    }
                }
            }
        }
    }

    private void handleNotification(String message)
    {
        System.out.println(TAG+" "+"firebase message="+message);
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext()))
        {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        }
        else
        {
            System.out.println(TAG+" "+"push in background");
            new SessionManager(this).setIsBackgroundNotification(true);
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void sendNotification(String messageBody)
    {
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("isFromNotification",true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    private void handleDataMessage(JSONObject json)
    {
        System.out.println(TAG+" "+"firebase json="+json);
        Log.e(TAG, "push json: " + json.toString());

        // send the complete json message to HomePageActivity through broadcast receiver
        if (json.toString()!=null && !json.toString().isEmpty())
        {
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("jsonMessage",json.toString());
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
        }

        try {
            Gson gson=new Gson();
            FcmNotificationMain fcmNotificationMain=gson.fromJson(json.toString(),FcmNotificationMain.class);

            String campaignId, imageUrl, title, message, type, userId, username,timestamp="";

            campaignId=fcmNotificationMain.getBody().getCampaignId();
            imageUrl = fcmNotificationMain.getBody().getImageUrl();
            title = fcmNotificationMain.getBody().getTitle();
            message = fcmNotificationMain.getBody().getMessage();
            type= fcmNotificationMain.getBody().getType();
            userId=fcmNotificationMain.getBody().getUserId();
            username=fcmNotificationMain.getBody().getUsername();

            System.out.println(TAG+" "+"campaignId="+campaignId+" "+"imageUrl="+imageUrl+" "+"title="+title+" "+"message="+message+" "+"type="+type+" "+"userId="+userId+" "+"username="+username);

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext()))
            {
                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), HomePageActivity.class);
                resultIntent.putExtra("message", message);

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    tempShowNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }

    /**
     * Showing notification with text only
     */
    private void tempShowNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        TempNotificationUtils notificationUtils = new TempNotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }
}
