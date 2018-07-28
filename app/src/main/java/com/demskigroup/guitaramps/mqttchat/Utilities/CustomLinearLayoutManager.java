package com.demskigroup.guitaramps.mqttchat.Utilities;
/*
 * Created by moda on 05/12/16.
 */

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;


/**
 * Disable predictive animations. There is a bug in RecyclerView which causes views that
 * are being reloaded to pull invalid ViewHolders from the internal recycler stack if the
 * adapter size has decreased since the ViewHolder was recycled.
 */
public class CustomLinearLayoutManager extends LinearLayoutManager {

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }

    public CustomLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }


}