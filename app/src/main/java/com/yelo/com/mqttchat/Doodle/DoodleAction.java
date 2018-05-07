package com.yelo.com.mqttchat.Doodle;

import android.graphics.Bitmap;

/**
 * Created by embed on 4/11/16.
 */
public interface DoodleAction {
    void doodleBitmap(Bitmap bitmap);

    void keyboardOpen();

    void KeyboardClose();
}

