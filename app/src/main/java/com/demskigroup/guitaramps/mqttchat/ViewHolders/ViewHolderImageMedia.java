package com.demskigroup.guitaramps.mqttchat.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.mqttchat.Utilities.AdjustableImageView;


/**
 * View holder for media history image recycler view item
 */

public class ViewHolderImageMedia extends RecyclerView.ViewHolder {

    public TextView fnf;
    public AdjustableImageView image;


    public ViewHolderImageMedia(View view) {
        super(view);
        fnf = (TextView) view.findViewById(R.id.fnf);
        image = (AdjustableImageView) view.findViewById(R.id.imageView28);
    }
}