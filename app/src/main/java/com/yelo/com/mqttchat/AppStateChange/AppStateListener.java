package com.yelo.com.mqttchat.AppStateChange;

/*
 * Created by moda on 23/02/17.
 */


/**
 * Listener for app state changes.
 */
public interface AppStateListener {

    /**
     * Called whenever the app comes into foreground.
     */
    void onAppDidEnterForeground();

    /**
     * Called whenever the app goes into background.
     */
    void onAppDidEnterBackground();

    //@formatter:off
    AppStateListener NO_OP = new AppStateListener() {
        @Override
        public void onAppDidEnterForeground() {
        }

        @Override
        public void onAppDidEnterBackground() {
        }
    };//@formatter:on
}