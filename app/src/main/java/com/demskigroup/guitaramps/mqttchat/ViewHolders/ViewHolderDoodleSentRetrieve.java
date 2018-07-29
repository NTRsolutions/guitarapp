package com.demskigroup.guitaramps.mqttchat.ViewHolders;

import android.graphics.Typeface;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.mqttchat.AppController;
import com.demskigroup.guitaramps.mqttchat.Utilities.RingProgressBar;


public class ViewHolderDoodleSentRetrieve extends RecyclerView.ViewHolder {

    public TextView time, date, fnf;

    public ImageView singleTick, doubleTickGreen, doubleTickBlue, clock;//,blocked;


    public AppCompatImageView imageView;

    public ImageView download;

    public RingProgressBar progressBar;

    public ProgressBar progressBar2;

    public ImageView cancel;

    public ViewHolderDoodleSentRetrieve(View view) {
        super(view);
        date = (TextView) view.findViewById(R.id.date);

        //  senderName = (TextView) view.findViewById(R.id.lblMsgFrom);

        imageView = (AppCompatImageView) view.findViewById(R.id.imgshow);

        time = (TextView) view.findViewById(R.id.ts);

        singleTick = (ImageView) view.findViewById(R.id.single_tick_green);

        doubleTickGreen = (ImageView) view.findViewById(R.id.double_tick_green);

        doubleTickBlue = (ImageView) view.findViewById(R.id.double_tick_blue);

        clock = (ImageView) view.findViewById(R.id.clock);


        fnf = (TextView) view.findViewById(R.id.fnf);


        progressBar = (RingProgressBar) view.findViewById(R.id.progress);
        cancel = (ImageView) view.findViewById(R.id.cancel);
        progressBar2 = (ProgressBar) view.findViewById(R.id.progress2);
        download = (ImageView) view.findViewById(R.id.download);


        Typeface tf = AppController.getInstance().getRobotoCondensedFont();
        time.setTypeface(tf, Typeface.ITALIC);

        date.setTypeface(tf, Typeface.ITALIC);
        fnf.setTypeface(tf, Typeface.NORMAL);
    }


}
