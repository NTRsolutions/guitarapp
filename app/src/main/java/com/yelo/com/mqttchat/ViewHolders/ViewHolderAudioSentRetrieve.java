package com.yelo.com.mqttchat.ViewHolders;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yelo.com.R;
import com.yelo.com.mqttchat.AppController;
import com.yelo.com.mqttchat.Utilities.RingProgressBar;


public class ViewHolderAudioSentRetrieve extends RecyclerView.ViewHolder {

    public TextView time, date, tv, fnf;
//    public TextView senderName;

    public ImageView singleTick, doubleTickGreen, doubleTickBlue, clock, playButton, download, cancel;//, blocked;

    public RingProgressBar progressBar;


    public ProgressBar progressBar2;


    public ViewHolderAudioSentRetrieve(View view) {
        super(view);


        // senderName = (TextView) view.findViewById(R.id.lblMsgFrom);


        tv = (TextView) view.findViewById(R.id.tv);
//        blocked = (ImageView) view.findViewById(R.id.blocked);
        playButton = (ImageView) view.findViewById(R.id.imageView26);
        date = (TextView) view.findViewById(R.id.date);

        time = (TextView) view.findViewById(R.id.ts);

        singleTick = (ImageView) view.findViewById(R.id.single_tick_green);

        doubleTickGreen = (ImageView) view.findViewById(R.id.double_tick_green);

        doubleTickBlue = (ImageView) view.findViewById(R.id.double_tick_blue);

        clock = (ImageView) view.findViewById(R.id.clock);


        fnf = (TextView) view.findViewById(R.id.fnf);


        progressBar2 = (ProgressBar) view.findViewById(R.id.progress2);
        progressBar = (RingProgressBar) view.findViewById(R.id.progress);
        download = (ImageView) view.findViewById(R.id.download);


        cancel = (ImageView) view.findViewById(R.id.cancel);


        Typeface tf = AppController.getInstance().getRobotoCondensedFont();
        time.setTypeface(tf, Typeface.ITALIC);

        date.setTypeface(tf, Typeface.ITALIC);
        fnf.setTypeface(tf, Typeface.NORMAL);
    }
}