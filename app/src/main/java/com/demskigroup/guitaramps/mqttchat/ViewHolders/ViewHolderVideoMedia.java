package com.demskigroup.guitaramps.mqttchat.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.mqttchat.Utilities.AdjustableImageView;

/**
 * View holder for media history video recycler view item
 */
public class ViewHolderVideoMedia extends RecyclerView.ViewHolder {


    public AdjustableImageView thumbnail;

    public TextView fnf;

    public  ViewHolderVideoMedia(View view) {
        super(view);
        fnf = (TextView) view.findViewById(R.id.fnf);
        thumbnail = (AdjustableImageView) view.findViewById(R.id.vidshow);
    }
}
