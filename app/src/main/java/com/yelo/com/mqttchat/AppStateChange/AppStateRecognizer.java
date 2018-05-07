package com.yelo.com.mqttchat.AppStateChange;

/*
 * Created by moda on 23/02/17.
 */

import android.support.annotation.NonNull;

interface AppStateRecognizer {

   void addListener(@NonNull AppStateListener listener);

   void removeListener(@NonNull AppStateListener listener);

   void start();

   void stop();

   @NonNull
   AppState getAppState();
}