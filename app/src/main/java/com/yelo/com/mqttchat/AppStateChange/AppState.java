package com.yelo.com.mqttchat.AppStateChange;

/*
 * Created by moda on 23/02/17.
 */

/**
 * States of the application(Background/foreground).
 */
enum AppState {

    /**
     * The app is in foreground, i.e. visible to the user.
     */
    FOREGROUND,

    /**
     * The app is in background, i.e. not visible to the user.
     */
    BACKGROUND
}