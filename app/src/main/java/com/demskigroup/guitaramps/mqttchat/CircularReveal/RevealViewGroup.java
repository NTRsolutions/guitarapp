package com.demskigroup.guitaramps.mqttchat.CircularReveal;

/*
 * Created by moda on 17/04/17.
 */
import android.view.ViewGroup;

/**
 * Indicator for internal API that {@link ViewGroup} support
 * Circular Reveal animation
 */
public interface RevealViewGroup {

    /**
     * @return Bridge between view and circular reveal animation
     */
    ViewRevealManager getViewRevealManager();

    /**
     *
     * @param manager ViewRevealManager
     */
    void setViewRevealManager(ViewRevealManager manager);
}