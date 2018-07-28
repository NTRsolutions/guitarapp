package com.demskigroup.guitaramps.mqttchat.AppStateChange;

/*
 * Created by moda on 23/02/17.
 */


import android.content.ComponentCallbacks2;
import android.content.res.Configuration;


class NoOpComponentCallbacks2 implements ComponentCallbacks2 {

    //@formatter:off
    @Override
    public void onTrimMemory(int level) {}
    @Override
    public void onConfigurationChanged(Configuration newConfig) {}
    @Override
    public void onLowMemory() {}
    //@formatter:on
}