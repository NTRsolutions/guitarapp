package com.demskigroup.guitaramps.mqttchat.ViewHolders;
/*
 * Created by moda on 02/04/16.
 */

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.mqttchat.AppController;
import com.demskigroup.guitaramps.mqttchat.Utilities.AdjustableImageView;
import com.demskigroup.guitaramps.mqttchat.Utilities.RingProgressBar;

/**
 * View holder for video received recycler view item
 */
public class ViewHolderVideoReceived extends RecyclerView.ViewHolder {

    public TextView senderName, time, date, fnf;


    public ImageView download, cancel;


    public ProgressBar progressBar2;

    public RingProgressBar progressBar;

    public AdjustableImageView thumbnail;


    public ViewHolderVideoReceived(View view) {
        super(view);
        date = (TextView) view.findViewById(R.id.date);

        // senderName = (TextView) view.findViewById(R.id.lblMsgFrom);


        time = (TextView) view.findViewById(R.id.ts);
        thumbnail = (AdjustableImageView) view.findViewById(R.id.vidshow);


        cancel = (ImageView) view.findViewById(R.id.cancel);

        progressBar2 = (ProgressBar) view.findViewById(R.id.progress2);
        progressBar = (RingProgressBar) view.findViewById(R.id.progress);


        download = (ImageView) view.findViewById(R.id.download);
        fnf = (TextView) view.findViewById(R.id.fnf);
        Typeface tf = AppController.getInstance().getRobotoCondensedFont();
        time.setTypeface(tf, Typeface.ITALIC);

        date.setTypeface(tf, Typeface.ITALIC);
        fnf.setTypeface(tf, Typeface.NORMAL);
    }
}
