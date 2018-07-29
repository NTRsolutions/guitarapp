package com.demskigroup.guitaramps.mqttchat.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.demskigroup.guitaramps.mqttchat.AppController;
/**
 *
 * @since  21/06/17.
 * @version 1.0.
 * @author 3Embed.
 */
public class AppKilled extends Service
{
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    public void onTaskRemoved(Intent rootIntent)
    {
        AppController.getInstance().disconnect();
        AppController.getInstance().setApplicationKilled(true);
        AppController.getInstance().createMQttConnection(AppController.getInstance().getUserId());
        stopSelf();
    }
}