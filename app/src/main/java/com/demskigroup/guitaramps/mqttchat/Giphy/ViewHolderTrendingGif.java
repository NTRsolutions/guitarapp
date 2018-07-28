package com.demskigroup.guitaramps.mqttchat.Giphy;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.demskigroup.guitaramps.R;


/**
 * Created by embed on 4/1/17.
 */
public class ViewHolderTrendingGif extends RecyclerView.ViewHolder {

    public ImageView image;

    public ViewHolderTrendingGif(View view) {
        super(view);
        image = (ImageView) view.findViewById(R.id.imageView29);
    }
}
