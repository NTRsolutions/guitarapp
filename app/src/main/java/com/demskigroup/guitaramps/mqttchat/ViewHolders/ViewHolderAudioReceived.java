package com.demskigroup.guitaramps.mqttchat.ViewHolders;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.demskigroup.guitaramps.R;
import com.demskigroup.guitaramps.mqttchat.AppController;
import com.demskigroup.guitaramps.mqttchat.Utilities.RingProgressBar;


/**
 * View holder for audio received recycler view item
 */
public class ViewHolderAudioReceived extends RecyclerView.ViewHolder {


//    public TextView senderName;

    public TextView time, date, tv, fnf;
    public RingProgressBar progressBar;


    public ImageView playButton, download;
    public ProgressBar progressBar2;


    public ImageView cancel;

    public ViewHolderAudioReceived(View view) {
        super(view);
        playButton = (ImageView) view.findViewById(R.id.imageView26);


        // senderName = (TextView) view.findViewById(R.id.lblMsgFrom);
        tv = (TextView) view.findViewById(R.id.tv);

        time = (TextView) view.findViewById(R.id.ts);

        date = (TextView) view.findViewById(R.id.date);
        progressBar2 = (ProgressBar) view.findViewById(R.id.progress2);
        progressBar = (RingProgressBar) view.findViewById(R.id.progress);
        download = (ImageView) view.findViewById(R.id.download);


        cancel = (ImageView) view.findViewById(R.id.cancel);

        fnf = (TextView) view.findViewById(R.id.fnf);

        Typeface tf = AppController.getInstance().getRobotoCondensedFont();
        time.setTypeface(tf, Typeface.ITALIC);

        date.setTypeface(tf, Typeface.ITALIC);
        tv.setTypeface(tf, Typeface.NORMAL);

        fnf.setTypeface(tf, Typeface.NORMAL);
    }

}
