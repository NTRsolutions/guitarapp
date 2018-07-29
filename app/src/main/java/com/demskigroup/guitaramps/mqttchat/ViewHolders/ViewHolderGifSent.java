package com.demskigroup.guitaramps.mqttchat.ViewHolders;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.mqttchat.AppController;


public class ViewHolderGifSent extends RecyclerView.ViewHolder {

    //    public TextView senderName;
    public TextView time, date;

    public ImageView singleTick, doubleTickGreen, doubleTickBlue, clock;

    public ImageView gifImage, stillGifImage;


    public ViewHolderGifSent(View view) {
        super(view);


//        senderName = (TextView) view.findViewById(R.id.lblMsgFrom);


        date = (TextView) view.findViewById(R.id.date);

        time = (TextView) view.findViewById(R.id.ts);

        singleTick = (ImageView) view.findViewById(R.id.single_tick_green);

        doubleTickGreen = (ImageView) view.findViewById(R.id.double_tick_green);

        doubleTickBlue = (ImageView) view.findViewById(R.id.double_tick_blue);

        clock = (ImageView) view.findViewById(R.id.clock);

        gifImage = (ImageView) view.findViewById(R.id.gifThumbnail);

        stillGifImage = (ImageView) view.findViewById(R.id.stillGifImage);

        Typeface tf = AppController.getInstance().getRobotoCondensedFont();
        time.setTypeface(tf, Typeface.ITALIC);

        date.setTypeface(tf, Typeface.ITALIC);

    }
}
