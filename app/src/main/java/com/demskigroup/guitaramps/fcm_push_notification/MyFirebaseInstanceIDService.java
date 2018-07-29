package com.demskigroup.guitaramps.fcm_push_notification;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.demskigroup.guitaramps.utility.SessionManager;

/**
 * <h>MyFirebaseInstanceIDService</h>
 * <p>
 *     In this class we used to get the Push Registration Id.
 * </p>
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService
{
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        Log.e(TAG, "sendRegistrationToServer: " + refreshedToken);

        // Saving reg id to shared preferences
        SessionManager mSessionManager = new SessionManager(MyFirebaseInstanceIDService.this);
        if (refreshedToken!=null && !refreshedToken.isEmpty())
            mSessionManager.setPushToken(refreshedToken);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }
}

