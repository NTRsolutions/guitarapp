package com.yelo.com.mqttchat.Utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yelo.com.mqttchat.AppController;


/**
 * Created by moda on 13/07/17.
 */


/**
 * For handling the time changes
 */
public class TimeChangedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

//Log.d("log39","time changed");
        AppController.getInstance().getCurrentTime();
        //Do whatever you need to
    }

}