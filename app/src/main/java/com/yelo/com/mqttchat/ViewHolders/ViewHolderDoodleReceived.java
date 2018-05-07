package com.yelo.com.mqttchat.ViewHolders;

import android.graphics.Typeface;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yelo.com.R;
import com.yelo.com.mqttchat.AppController;
import com.yelo.com.mqttchat.Utilities.RingProgressBar;

public class ViewHolderDoodleReceived extends RecyclerView.ViewHolder {


    //    public TextView senderName;
    public TextView time, date, fnf;


    public AppCompatImageView imageView;

    public ImageView download;

    public RingProgressBar progressBar;

    public ProgressBar progressBar2;

    public ImageView cancel;

    public ViewHolderDoodleReceived(View view) {
        super(view);

        // senderName = (TextView) view.findViewById(R.id.lblMsgFrom);
        date = (TextView) view.findViewById(R.id.date);
        imageView = (AppCompatImageView) view.findViewById(R.id.imgshow);

        time = (TextView) view.findViewById(R.id.ts);

        progressBar = (RingProgressBar) view.findViewById(R.id.progress);


        cancel = (ImageView) view.findViewById(R.id.cancel);
        progressBar2 = (ProgressBar) view.findViewById(R.id.progress2);
        download = (ImageView) view.findViewById(R.id.download);
        fnf = (TextView) view.findViewById(R.id.fnf);
        Typeface tf = AppController.getInstance().getRobotoCondensedFont();
        time.setTypeface(tf, Typeface.ITALIC);

        date.setTypeface(tf, Typeface.ITALIC);
        fnf.setTypeface(tf, Typeface.NORMAL);
    }
}
