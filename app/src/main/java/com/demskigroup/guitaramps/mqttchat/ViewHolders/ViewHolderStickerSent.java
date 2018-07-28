package com.demskigroup.guitaramps.mqttchat.ViewHolders;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.mqttchat.AppController;


public class ViewHolderStickerSent extends RecyclerView.ViewHolder {


//    public TextView  time, date;

    public TextView time, date;

    public ImageView singleTick, doubleTickGreen, doubleTickBlue, clock, imageView;

    public ViewHolderStickerSent(View view) {
        super(view);

//        senderName = (TextView) view.findViewById(R.id.StickerslblMsgFrom);

        imageView = (ImageView) view.findViewById(R.id.imgshow);

        date = (TextView) view.findViewById(R.id.date);

        time = (TextView) view.findViewById(R.id.ts);

        singleTick = (ImageView) view.findViewById(R.id.single_tick_green);

        doubleTickGreen = (ImageView) view.findViewById(R.id.double_tick_green);

        doubleTickBlue = (ImageView) view.findViewById(R.id.double_tick_blue);

        clock = (ImageView) view.findViewById(R.id.clock);
        Typeface tf = AppController.getInstance().getRobotoCondensedFont();
        time.setTypeface(tf, Typeface.ITALIC);

        date.setTypeface(tf, Typeface.ITALIC);

    }
}
