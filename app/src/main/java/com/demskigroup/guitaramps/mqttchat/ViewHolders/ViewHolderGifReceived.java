package com.demskigroup.guitaramps.mqttchat.ViewHolders;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.mqttchat.AppController;

public class ViewHolderGifReceived extends RecyclerView.ViewHolder {

    //    public TextView senderName;
    public TextView time, date;


    public ImageView gifImage, gifStillImage;


    public ViewHolderGifReceived(View view) {
        super(view);

//        senderName = (TextView) view.findViewById(R.id.lblMsgFrom);


        gifImage = (ImageView) view.findViewById(R.id.vidshow);


        date = (TextView) view.findViewById(R.id.date);
        time = (TextView) view.findViewById(R.id.ts);

        gifStillImage = (ImageView) view.findViewById(R.id.gifStillImage);
        Typeface tf = AppController.getInstance().getRobotoCondensedFont();
        time.setTypeface(tf, Typeface.ITALIC);

        date.setTypeface(tf, Typeface.ITALIC);

    }
}
