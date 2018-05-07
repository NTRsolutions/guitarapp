package com.yelo.com.mqttchat.Utilities;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.yelo.com.mqttchat.AppController;
/**
 * @since 13/07/17.
 */
public class ShutdownReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        //Insert code here
        if (!AppController.getInstance().getSharedPreferences().getBoolean("applicationKilled", true))
        {
            AppController.getInstance().disconnect();
            AppController.getInstance().setApplicationKilled(true);
        }
    }
}