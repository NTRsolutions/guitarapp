package com.yelo.com.mqttchat.AppStateChange;

/*
 * Created by moda on 23/02/17.
 */

import android.support.annotation.NonNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


class CompositeAppStateListener implements AppStateListener {

    @NonNull
    private final List<AppStateListener> listeners = new CopyOnWriteArrayList<>();

    @Override
    public void onAppDidEnterForeground() {
        for (AppStateListener listener : listeners) {
            listener.onAppDidEnterForeground();
        }
    }

    @Override
    public void onAppDidEnterBackground() {
        for (AppStateListener listener : listeners) {
            listener.onAppDidEnterBackground();
        }
    }

    void addListener(@NonNull AppStateListener listener) {
        listeners.add(listener);
    }

    void removeListener(@NonNull AppStateListener listener) {
        listeners.remove(listener);
    }
}