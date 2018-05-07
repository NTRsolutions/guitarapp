package com.yelo.com.mqttchat.Utilities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import com.yelo.com.mqttchat.AppController;
/*
 * To keep the device awake when it is booting
 */
public class BootCompletedIntentReceiver extends WakefulBroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        AppController.getInstance().createMQttConnection(AppController.getInstance().getUserId());

    }
}